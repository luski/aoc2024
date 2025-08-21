import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const rl = createInterface({ input, output });

const lines: string[] = [];

for await (const line of rl) {
  if (line.trim() === "") break;
  lines.push(line.trim());
}

rl.close();

const [g1, g2] = lines.reduce(
  (acc, line) => {
    const [a, b] = line.split(/\s+/).map(Number);
    acc[0].push(a);
    acc[1].push(b);
    return acc;
  },
  [[], []] as number[][],
);
g1.sort();
g2.sort();

let result = 0;
for (let i = 0; i < g1.length; i++) {
  result += Math.abs(g1[i] - g2[i]);
}

console.log("Part 1:", result);

result = 0;

for (const id of g1) {
  const firstIndex = g2.indexOf(id);
  if (firstIndex !== -1) result += id * (g2.lastIndexOf(id) - firstIndex + 1);
}

console.log("Part 2:", result);
