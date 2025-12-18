#!/usr/bin/env python3
"""
tools/remove_emojis.py
Usage:
  - List files with emojis: python3 tools/remove_emojis.py --check
  - Remove emojis (create backups *.bak): python3 tools/remove_emojis.py --apply

This script targets common source and doc extensions; it creates backups before modifying files.
"""
import argparse
import re
from pathlib import Path

EXTS = {'.js', '.jsx', '.ts', '.tsx', '.java', '.properties', '.xml', '.html', '.css', '.md', '.json', '.yml', '.yaml'}
SKIP_DIRS = {'node_modules', 'target', '.git', 'dist', 'build'}
# Broad emoji regex covering many blocks
EMOJI_RE = re.compile(
    '['
    '\U0001F300-\U0001F5FF'
    '\U0001F600-\U0001F64F'
    '\U0001F680-\U0001F6FF'
    '\U0001F700-\U0001F77F'
    '\U0001F780-\U0001F7FF'
    '\U0001F800-\U0001F8FF'
    '\U0001F900-\U0001F9FF'
    '\U0001FA00-\U0001FA6F'
    '\u2600-\u26FF'
    '\u2700-\u27BF'
    ']', flags=re.UNICODE)


def is_text_file(path: Path) -> bool:
    try:
        with open(path, 'rb') as f:
            chunk = f.read(4096)
            if b'\x00' in chunk:
                return False
        return True
    except Exception:
        return False


def iter_files(root: Path):
    for p in root.rglob('*'):
        # skip directories
        if any(part in SKIP_DIRS for part in p.parts):
            continue
        if p.is_file() and p.suffix.lower() in EXTS and is_text_file(p):
            yield p


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--apply', action='store_true')
    ap.add_argument('--check', action='store_true')
    ap.add_argument('--root', default='.')
    args = ap.parse_args()

    root = Path(args.root)
    found = []
    for f in iter_files(root):
        try:
            text = f.read_text(encoding='utf-8')
        except Exception:
            try:
                text = f.read_text(encoding='latin-1')
            except Exception:
                continue
        if EMOJI_RE.search(text):
            found.append(f)

    if args.check:
        if not found:
            print('No emojis found.')
            return
        print('Files containing emojis:')
        for p in found:
            print(p)
        return

    if args.apply:
        if not found:
            print('No emojis found. Nothing to apply.')
            return
        for p in found:
            try:
                orig = p.read_text(encoding='utf-8', errors='ignore')
                bak = p.with_suffix(p.suffix + '.bak')
                bak.write_text(orig, encoding='utf-8')
                new = EMOJI_RE.sub('', orig)
                if new != orig:
                    p.write_text(new, encoding='utf-8')
                    print(f'Processed: {p} (backup: {bak.name})')
            except Exception as e:
                print(f'Error processing {p}: {e}')
        print('Done. Please review backups (.bak) before committing.')
        return

    print('Specify --check or --apply')

if __name__ == '__main__':
    main()
