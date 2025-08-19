import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Pt = [number, number];
type Desc = {
  a: Pt;
  b: Pt;
  prize: Pt;
};

function solve(data: Desc[], offset: Pt = [0, 0]) {
  let result = 0;
  let index = 0;
  for (const { a, b, prize } of data) {
    const p = [prize[0] + offset[0], prize[1] + offset[1]];
    let delta = a[0] * b[1] - a[1] * b[0];
    let x = (p[0] * b[1] - p[1] * b[0]) / delta;
    let y = (a[0] * p[1] - a[1] * p[0]) / delta;
    if (Math.floor(x) === x && Math.floor(y) === y) {
      result += 3 * x + y;
    }
  }
  return result;
}

const data = await readInput();
console.log("Part 1", solve(data));
console.log("Part 2", solve(data, [10000000000000, 10000000000000]));

async function readInput() {
  function parseBtn(line: string): Pt {
    const [_, v] = line.split(": ");
    const [x, y] = v.split(", ");

    const [_a, xVal] = x.split("+");
    const [_b, yVal] = y.split("+");
    return [Number(xVal), Number(yVal)];
  }

  function parsePrize(line: string): Pt {
    const [_, v] = line.split(": ");
    const [x, y] = v.split(", ");

    const [_a, xVal] = x.split("=");
    const [_b, yVal] = y.split("=");
    return [Number(xVal), Number(yVal)];
  }

  const rl = createInterface({ input, output });
  const lines = ["", "", ""];
  let lineNumber = 0;
  const result: Desc[] = [];
  for await (const line of rl) {
    if (lineNumber === 0 && line.trim() === "") {
      break;
    }
    if (lineNumber < 3) {
      lines[lineNumber] = line.trim();
    } else {
      result.push({
        a: parseBtn(lines[0]),
        b: parseBtn(lines[1]),
        prize: parsePrize(lines[2]),
      });
    }
    lineNumber = (lineNumber + 1) % 4;
  }
  result.push({
    a: parseBtn(lines[0]),
    b: parseBtn(lines[1]),
    prize: parsePrize(lines[2]),
  });
  rl.close();
  return result;
}
