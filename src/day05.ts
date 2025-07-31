import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const { rules, paths } = await readInput();

console.log("Part 1:", solution1());
console.log("Part 2:", solution2());

async function readInput() {
  const rl = createInterface({ input, output });

  const rules = new Map<string, Set<string>>();
  const paths: string[][] = [];

  for await (const line of rl) {
    if (line.trim() === "") break;
    const [from, to] = line.trim().split("|");
    if (rules.has(from)) {
      rules.get(from)?.add(to);
    } else {
      rules.set(from, new Set([to]));
    }
  }
  for await (const line of rl) {
    if (line.trim() === "") break;
    paths.push(line.trim().split(","));
  }
  rl.close();

  return { rules, paths };
}

function solution1() {
  let result = 0;

  for (const path of paths) {
    if (isOrdered(path, rules)) {
      result += Number(getMiddleElement(path));
    }
  }
  return result;
}

function solution2() {
  let result = 0;

  for (const path of paths) {
    if (isOrdered(path, rules)) continue;
    const sortedPath = path.toSorted((a, b) => {
      if (rules.get(a)?.has(b)) return -1;
      if (rules.get(b)?.has(a)) return 1;
      return 0;
    });
    result += Number(getMiddleElement(sortedPath));
  }

  return result;
}

function isOrdered([head, ...tail]: string[], rules: Map<string, Set<string>>) {
  if (tail.length === 0) {
    return true;
  }
  for (const elem of tail) {
    if (rules.get(elem)?.has(head)) {
      return false;
    } else {
      return isOrdered(tail, rules);
    }
  }
}

function getMiddleElement(path: string[]) {
  return path[Math.floor(path.length / 2)];
}
