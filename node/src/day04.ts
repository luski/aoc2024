import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const DIRECTIONS = [
  [-1, -1],
  [-1, 0],
  [-1, 1],
  [0, -1],
  [0, 0],
  [0, 1],
  [1, -1],
  [1, 0],
  [1, 1],
];
const CHARS_TO_CHECK = ["X", "M", "A", "S"];

const rl = createInterface({ input, output });

const chars: string[][] = [];

for await (const line of rl) {
  if (line.trim() === "") break;
  chars.push(line.trim().split(""));
}
rl.close();

let result = 0;

for (let i = 0; i < chars.length; i++) {
  for (let j = 0; j < chars[0].length; j++) {
    if (chars[i][j] === "X") {
      result += calculateOccurrencesPart1(chars, i, j);
    }
  }
}

console.log("Part 1:", result);
result = 0;

for (let i = 0; i < chars.length; i++) {
  for (let j = 0; j < chars[0].length; j++) {
    if (chars[i][j] === "A") {
      result += calculateOccurrencesPart2(chars, i, j);
    }
  }
}

console.log("Part 2:", result);

function calculateOccurrencesPart1(chars: string[][], i: number, j: number) {
  let result = 0;

  for (const [di, dj] of DIRECTIONS) {
    let found = true;
    for (let m = 0; m < CHARS_TO_CHECK.length; m++) {
      const matchingChar = CHARS_TO_CHECK[m];
      const indices = [i + di * m, j + dj * m];
      if (
        !validIndices(chars, indices) ||
        chars[indices[0]][indices[1]] !== matchingChar
      ) {
        found = false;
        break;
      }
    }
    if (found) result += 1;
  }
  return result;
}

function validIndices(chars: string[][], [i, j]: number[]) {
  return (
    i >= 0 && i < chars.length && j >= 0 && j < chars[i].length && chars[i][j]
  );
}

function calculateOccurrencesPart2(chars: string[][], i: number, j: number) {
  let result = 0;

  const chars0 = extractChars(chars, [i - 1, j - 1], [i + 1, j + 1]);
  const chars1 = extractChars(chars, [i + 1, j - 1], [i - 1, j + 1]);
  if (
    chars0.includes("M") &&
    chars0.includes("S") &&
    chars1.includes("M") &&
    chars1.includes("S")
  ) {
    result += 1;
  }
  return result;
}

function extractChars(chars: string[][], ...points: [number, number][]) {
  return points.map((point) => {
    if (!validIndices(chars, point)) {
      return null;
    }
    return chars[point[0]][point[1]];
  });
}
