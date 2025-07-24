import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const rl = createInterface({ input, output });

const lines: string[] = [];

for await (const line of rl) {
  if (line.trim() === "") break;
  lines.push(line.trim());
}
rl.close();
const inputData = lines.join("");

console.log("Part 1:", calculate(inputData));

let result = 0;
for (const line of extractActiveBlocks(inputData)) {
  result += calculate(line);
}

console.log("Part 2:", result);

function extractActiveBlocks(line: string): string[] {
  const [first, ...rest] = line.split("don't()");
  return [
    first,
    ...rest.map((subline) => {
      return subline.split("do()").slice(1).join("");
    }),
  ].filter(Boolean);
}

function calculate(inputData: string) {
  let result = 0;

  const matches = [
    ...inputData.matchAll(/mul\((?<first>\d{1,3}),(?<second>\d{1,3})\)/g),
  ];

  for (const match of matches) {
    const { first, second } = match.groups as unknown as {
      first: string;
      second: string;
    };
    result += Number(first) * Number(second);
  }

  return result;
}
