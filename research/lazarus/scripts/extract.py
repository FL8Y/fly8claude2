# Extract Pokemon Lazarus (pokeemerald-expansion based) data tables to JSON.
import sys, struct, json
sys.path.insert(0, sys.argv[1]); from g3 import dec
d = open(sys.argv[2], 'rb').read(); OUT = sys.argv[3]
P = lambda p: p - 0x08000000
u16 = lambda a: struct.unpack_from('<H', d, a)[0]
u32 = lambda a: struct.unpack_from('<I', d, a)[0]
def txt(a, n=4000):
    s, _ = dec(d, a, n); return s
def ptxt(p, n=4000): return txt(P(p), n) if 0x08000000 <= p < 0x0a000000 else None

TYPES_T = 0x57225c; TYPE_ST = 40
types = []
for i in range(0, 25):
    s = txt(TYPES_T + i * TYPE_ST, 13)
    if s is None: break
    types.append(s)

AB_T = 0x8dd968; AB_ST = 28
abilities = []
i = 0
while True:
    s = txt(AB_T + i * AB_ST, 17)
    if s is None: break
    desc = ptxt(u32(AB_T + i * AB_ST + 20), 200) if 0x08000000 <= u32(AB_T + i * AB_ST + 20) < 0x0a000000 else None
    abilities.append({'id': i, 'name': s, 'desc': desc}); i += 1

MV_T = 0x8cfac8; MV_ST = 52
moves = []
for i in range(937):
    a = MV_T + i * MV_ST
    w = u16(a + 10)
    moves.append({'id': i, 'name': ptxt(u32(a), 30), 'desc': ptxt(u32(a + 4), 300), 'effect': u16(a + 8),
                  'type': w & 31, 'category': ['Physical', 'Special', 'Status'][(w >> 5) & 3] if ((w >> 5) & 3) < 3 else '?',
                  'power': w >> 7, 'accuracy': d[a + 12], 'target': d[a + 13], 'pp': d[a + 14], 'priority': struct.unpack_from('<b', d, a + 15)[0]})

IT_T = 0x86855c; IT_ST = 80
items = []
i = 0
while True:
    s = txt(IT_T + i * IT_ST, 20)
    if s is None or (i > 10 and s == ''): break
    items.append({'id': i, 'name': s}); i += 1

SP_T = 0xc7a338; SP_ST = 212; SP_N = 1561
def lvl_moves(p):
    out = []; a = P(p)
    while 0 <= a < len(d) and len(out) < 200:
        m, l = u16(a), u16(a + 2)
        if m == 0xFFFF: break
        out.append([m, l]); a += 4
    return out
def u16list(p, cap=400):
    out = []; a = P(p)
    while 0 <= a < len(d) and len(out) < cap:
        m = u16(a)
        if m == 0xFFFF: break
        out.append(m); a += 2
    return out
def evos(p):
    out = []; a = P(p)
    while len(out) < 16:
        m = u16(a)
        if m == 0xFFFF: break
        out.append([m, u16(a + 2), u16(a + 4)]); a += 8
    return out
species = []
for i in range(SP_N):
    a = SP_T + i * SP_ST
    if not any(d[a:a + SP_ST]): continue
    name = txt(a + 0x2c, 13)
    if not name: continue
    ptr = lambda o: u32(a + o)
    ok = lambda p: 0x08000000 <= p < 0x0a000000
    ev = u16(a + 12)
    species.append({
        'id': i, 'name': name, 'category': txt(a + 31, 13),
        'stats': list(d[a:a + 6]), 'types': [d[a + 6], d[a + 7]], 'catchRate': d[a + 8], 'expYield': u16(a + 10),
        'evYield': [(ev >> s) & 3 for s in (0, 2, 4, 6, 8, 10)], 'itemCommon': u16(a + 14), 'itemRare': u16(a + 16),
        'genderRatio': d[a + 18], 'eggCycles': d[a + 19], 'friendship': d[a + 20], 'growthRate': d[a + 21],
        'eggGroups': [d[a + 22], d[a + 23]], 'abilities': [u16(a + 24), u16(a + 26), u16(a + 28)],
        'natDex': u16(a + 60), 'height': u16(a + 62), 'weight': u16(a + 64),
        'dexText': ptxt(ptr(76), 400) if ok(ptr(76)) else None,
        'levelUp': lvl_moves(ptr(144)) if ok(ptr(144)) else [],
        'teachable': u16list(ptr(148)) if ok(ptr(148)) else [],
        'eggMoves': u16list(ptr(152), 60) if ok(ptr(152)) else [],
        'evolutions': evos(ptr(156)) if ok(ptr(156)) else [],
        'forms': u16list(ptr(160), 40) if ok(ptr(160)) else [],
    })
json.dump({'types': types, 'abilities': abilities, 'moves': moves, 'items': items, 'species': species}, open(OUT, 'w'), indent=0)
print('types', len(types), types)
print('abilities', len(abilities), 'moves', len(moves), 'items', len(items), 'species', len(species))
