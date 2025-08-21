import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const testCases = await readInput();
console.log("Part 1:", solution1());
console.log("Part 2:", solution2());

async function readInput() {
  const rl = createInterface({ input, output });

  const result: [number, number[]][] = [];

  for await (const line of rl) {
    if (line.trim() === "") break;
    const [sumStr, elementsStr] = line.split(":");
    result.push([Number(sumStr), elementsStr.trim().split(" ").map(Number)]);
  }
  rl.close();

  return result;
}

function solution1() {
  return solve(2);
}

function solution2() {
  return solve(3);
}

function next(
  acc: number[],
  index: number,
  values: number,
  callback: (permutation: number[]) => boolean,
) {
  if (acc.length === index) {
    return callback(acc);
  }

  for (let i = 0; i < values; i++) {
    acc[index] = i;
    const result = next(acc, index + 1, values, callback);
    if (result) {
      return true;
    }
  }

  return false;
}
function solve(num: number) {
  return testCases
    .filter(([sum, elements]) => {
      const signs = new Array(elements.length - 1).fill(0);
      return next(signs, 0, num, () => calculate(elements, signs) === sum);
    })
    .reduce((acc, [sum]) => acc + sum, 0);
}

function calculate(elements: number[], signs: number[]) {
  let result = elements[0];
  for (let i = 1; i < elements.length; i++) {
    if (signs[i - 1] === 0) {
      result += elements[i];
    } else if (signs[i - 1] === 1) {
      result *= elements[i];
    } else {
      result = parseInt(`${result}${elements[i]}`);
    }
  }
  return result;
}
