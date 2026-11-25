from argparse import ArgumentParser
from pathlib import Path
from zipfile import ZipFile

parser = ArgumentParser()
parser.add_argument("apk", type=Path)
parser.add_argument("platform", choices=("mobile", "tv"))
args = parser.parse_args()

with ZipFile(args.apk) as apk:
    names = apk.namelist()
    dex = [apk.read(n) for n in names if n.startswith("classes") and n.endswith(".dex")]
    expected = b"MobileVizoHomeKt" if args.platform == "mobile" else b"TvVizoHomeKt"
    assert any(expected in entry for entry in dex), "YAiPlayer Vizo adapter missing"
    assert any(b"Lapp/yai/shells/vizo/ShellModes;" in entry for entry in dex), "Shell mode selector missing"
    assert any(b"Lapp/yai/shells/mate/MateHomeKt;" in entry for entry in dex) == (args.platform == "tv"), "Incorrect Mate platform"
    abis = {n.split("/")[1] for n in names if n.startswith("lib/") and n.endswith(".so")}
    assert {"arm64-v8a", "armeabi-v7a"} <= abis, f"Missing supported ARM ABIs: {abis}"
    if args.platform == "mobile":
        assert "assets/tvoptimizer/launcher.apk" in names, "Existing TV optimizer launcher asset missing"
