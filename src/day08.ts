import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Pt = [number, number];

const { result: map, w, h } = await readInput();
console.log("Part 1:", solve(true));
console.log("Part 2:", solve(false));

async function readInput() {
  const rl = createInterface({ input, output });
  let w = 0;
  let i = 0;

  const result = new Map<string, Pt[]>();

  for await (const line of rl) {
    if (line.trim() === "") break;
    w = line.length;
    for (let j = 0; j < line.length; j++) {
      const char = line.charAt(j);
      if (char === ".") continue;

      if (result.has(char)) {
        result.get(char)!.push([i, j]);
      } else {
        result.set(char, [[i, j]]);
      }
    }
    i++;
  }
  rl.close();

  return { result, w, h: i };
}

function solve(limited: boolean) {
  const result: Set<string> = new Set();

  for (const pairs of map.values()) {
    for (let i = 0; i < pairs.length; i++) {
      for (let j = i + 1; j < pairs.length; j++) {
        const v = vector(pairs[i], pairs[j]);
        executeMultiple(pairs[j], v, add, limited, result);
        executeMultiple(pairs[i], v, subtract, limited, result);
        if (!limited) {
          result.add(hash(pairs[i]));
          result.add(hash(pairs[j]));
        }
      }
    }
  }
  return result.size;
}

function executeMultiple(
  a: Pt,
  b: Pt,
  operation: (a: Pt, b: Pt) => Pt,
  limited: boolean,
  acc: Set<string>,
) {
  do {
    a = operation(a, b);
    if (inBounds(a, w, h)) acc.add(hash(a));
    else break;
  } while (!limited);
}

function vector([a_i, a_j]: Pt, [b_i, b_j]: Pt): Pt {
  return [b_i - a_i, b_j - a_j];
}

function add([a_i, a_j]: Pt, [v_i, v_j]: Pt): Pt {
  return [a_i + v_i, a_j + v_j];
}

function subtract([a_i, a_j]: Pt, [v_i, v_j]: Pt): Pt {
  return [a_i - v_i, a_j - v_j];
}

function inBounds([i, j]: Pt, w: number, h: number) {
  return i >= 0 && j >= 0 && i < h && j < w;
}

function hash([i, j]: Pt) {
  return `${i}|${j}`;
}
