import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const rl = createInterface({ input, output });

const lines: string[] = [];

for await (const line of rl) {
  if (line.trim() === "") break;
  lines.push(line.trim());
}

rl.close();

const result1 = lines.filter((line) => {
  const values = line.split(" ").map(Number);
  return isSafe(values);
}).length;

console.log("Part 1:", result1);

const result2 = lines.filter((line) => {
  const values = line.split(" ").map(Number);
  return isSafe(values) || isAlmostSafe(values);
}).length;

console.log("Part 2:", result2);

function isSafe(values: number[]) {
  let [current, ...rest] = values;
  const decreasing = current > rest[0];

  for (let value of rest) {
    if (
      Math.abs(value - current) > 3 ||
      Math.abs(value - current) === 0 ||
      decreasing !== current > value
    ) {
      return false;
    }
    current = value;
  }

  return true;
}

function isAlmostSafe(values: number[]) {
  for (let i = 0; i < values.length; i++) {
    if (isSafe([...values.slice(0, i), ...values.slice(i + 1)])) {
      return true;
    }
  }
  return false;
}
