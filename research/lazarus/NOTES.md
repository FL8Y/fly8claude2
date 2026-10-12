# Pokémon Lazarus v2 research notes (12 Oct 2026)

**Goal:** a reference data set for a later Roblox Studio project, with an original name for every creature.
**Input:** `Copy_of_Pokemon_Lazarus_v2.7z`. It holds one GBA ROM, `Copy of Pokemon Lazarus v2.gba`
(32 MB, SHA-256 d756b278…6e97). The ROM header is POKEMON EMER / BPEE, built on pokeemerald-expansion with Gen 1–9 species.
The ROM is not committed. REA cannot open 7z or GBA, so the data was parsed with the custom scripts in `scripts/`.

## Status
- DONE: data extraction (`scripts/extract.py` → `data/lazarus_data.json`, Pokédex text stripped).
- DONE: trainers (`data/trainers_lazarus.json`, 285 Lazarus trainers) and map sections (`data/mapsections.json`).
- DONE: original names for all 454 species (`scripts/names.py`). Checked: no gaps, no duplicates, no clash with existing species, move, item or ability names.
- TODO: form labels (Mega and regional forms: 668 entries but 454 names), renaming branded terms
  (Poké Ball and others; Mega Stone item names contain species names), Roblox Luau ModuleScripts + JSON export, and the .docx reference.

## ROM layout (file offsets)
- Text: Gen 3 charmap, 0xFF ends a string (`scripts/g3.py`).
- Species: gSpeciesInfo at 0xC7A338, 212 bytes per entry, 1561 slots (668 filled).
  Fields: +0 stats (HP, Atk, Def, Spe, SpA, SpD); +6/7 types; +8 catch rate; +10 exp (u16); +12 EV bits; +14/16 items;
  +18 gender; +19 egg cycles; +20 friendship; +21 growth rate; +22/23 egg groups; +24/26/28 abilities (u16);
  +31 category name [13]; +44 name [13]; +60 national dex number; +62 height; +64 weight; +76 → Pokédex text;
  +144 → level-up moves (u16 move, u16 level, ends 0xFFFF); +148 → TM/tutor moves (u16 list); +152 → egg moves;
  +156 → evolutions (8 bytes: method, param, target); +160 → form list.
- Types: 0x57225C, 40 bytes each, 22 entries (0 = None, 1 = Normal, …, 13 = Grass, 14 = Electric …).
- Moves: 0x8CFAC8, 52 bytes each, 937 moves. +0 → name; +4 → description; +8 effect;
  u16 at +10: type (5 bits), category (2 bits), power (9 bits); +12 accuracy; +13 target; +14 PP; +15 priority.
- Abilities: 0x8DD968, 28 bytes each, 319 entries, name stored inline.
- Items: 0x86855C, 80 bytes each, 722 entries, name stored inline.
- Map sections: 0xCE40DC, 8 bytes (x, y, w, h, → name). Ilios names are the mixed-case entries (index 4–40).
- Trainers: first name at 0x5DA883, 36 bytes each. Party pointer at name−15, party size at name+13.
  Party member, 36 bytes: +12 moves[4], +20 species, +22 item, +26 level.
- Evolution methods seen: 3 level, 4 trade, 6 item, 10/11 Silcoon/Cascoon, 12/13 Ninjask/Shedinja, 15/16 level female/male,
  17/18 level night/day, 19 Lycanroc form, 22 knows move, 27 level in rain, 29 Dark type in party, 46 use move many times,
  47/48 Basculegion, 51 walk steps.
- Lazarus changes from the original games: re-typings (e.g. Doduo Ground/Flying, Noctowl Ghost/Flying, Typhlosion Fire/Ground,
  Meganium Grass/Fairy); item evolutions (Gallade, Gliscor, the Eeveelutions); Raikou→Raging Bolt, Entei→Gouging Fire,
  Suicune→Walking Wake; Mega forms (e.g. Delphox, Greninja, Feraligatr).

## Story (summary, not dialogue)
The Ilios Region is Greek-themed. The player arrives as a field assistant for Acrisia University (Prof. Elia), with Rania and Niko as rival-friends.
The greeting is "Chaire". "Lazarus Taxa" are species once thought extinct that are now reappearing.
Team Chimera steals Pokémon, Mega Stones and the two halves of an Ancient Mechanism (from Kaptara Island and the Marmaro Mines)
to control land, sea and sky. Their leader is Kalliope, the missing 8th Gym Leader, who took over Areios Town (now Areios Hideout).
They summon the Raikou/Entei/Suicune trio; Kyogre, Groudon and Rayquaza resist them.
The player recovers a Key Stone from the hideout near Sofos City / Satyr's Lair, beats Kalliope, gets the Myth Badge, and the trio is freed.
A new Elite Four of Muses forms at the Ruins of Ahiyawa; the player becomes the first Champion. Postgame: Wakewater Isle, rematches.

Gyms (order and level of the first team):
1. Polymnia, Jusmail Town, Harvest Badge (Lv 10)
2. Ourani, Kalami City (Lv 17)
3. Kleio, Pythios Town (Lv 20)
4. Terpsikore, Sofos City (Lv 25)
5. Euterpe, Myrrini Island, Festival Badge (Lv 29)
6. Thalia & Melpomene, Fresco Isles, double battle (Lv 34)
7. Rhadine, Palati City, Passion Badge (Lv 38)
8. Kalliope, Areios, Myth Badge (Lv 43)

Elite Four rematches are Lv 52–53. Anyalios's team is Lv 55–56 (probably the final league battle).
Other badge names in the text: Gemstone, Grave, Wisdom, Theatrical (exact assignment not yet confirmed).
Locations: see `data/mapsections.json`.

## Regenerate
1. Extract the 7z (py7zr).
2. `python3 -I scripts/extract.py scripts rom.gba data.json`
3. The string dump, trainer extraction and map extraction were inline scripts; their offsets are listed above.
