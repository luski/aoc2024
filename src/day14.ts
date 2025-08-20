import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Pt = [number, number];

const data = await readInput();
console.log("Part 1", solve(101, 103, 100).score);
console.log("Part 2", part2());

function part2() {
  let found = false;
  let steps = 1;

  if (found) {
    clearCanvas(103);
    found = false;
  }
  while (!found) {
    let newData = solve(101, 103, steps++).newData;
    found = draw(newData, 101, 103);
  }

  return steps - 1;
}

async function readInput() {
  const rl = createInterface({ input, output });

  const data: [Pt, Pt][] = [];

  for await (const line of rl) {
    if (line.trim() === "") break;
    data.push(
      line
        .split(" ")
        .map((part) => part.split("=").at(-1))
        .map((part) => part!.split(",").map(Number) as Pt) as [Pt, Pt],
    );
  }

  rl.close();
  return data;
}

function solve(w: number, h: number, steps: number) {
  const sides = [0, 0, 0, 0];
  const bounds: [Pt, Pt][] = [
    [
      [0, 0],
      [Math.floor(w / 2) - 1, Math.floor(h / 2) - 1],
    ],
    [
      [Math.ceil(w / 2), 0],
      [w - 1, Math.floor(h / 2) - 1],
    ],
    [
      [0, Math.ceil(h / 2)],
      [Math.floor(w / 2) - 1, h - 1],
    ],
    [
      [Math.ceil(w / 2), Math.ceil(h / 2)],
      [w - 1, h - 1],
    ],
  ];

  let newData: Pt[] = [];
  for (const [p, v] of data) {
    const pos: Pt = [c(p[0], v[0], steps, w), c(p[1], v[1], steps, h)];
    newData.push(pos);
    for (let i = 0; i < bounds.length; i++) {
      if (inBounds(pos, bounds[i])) {
        sides[i]++;
        break;
      }
    }
  }
  return { score: sides.reduce((acc, score) => acc * score, 1), newData };
}

function c(start: number, v: number, steps: number, size: number) {
  const result = (start + v * steps) % size;
  if (result < 0) {
    return result + size;
  } else {
    return result;
  }
}

function inBounds(pt: Pt, bounds: [Pt, Pt]) {
  return (
    pt[0] >= bounds[0][0] &&
    pt[0] <= bounds[1][0] &&
    pt[1] >= bounds[0][1] &&
    pt[1] <= bounds[1][1]
  );
}

function clearCanvas(h: number) {
  for (let i = 0; i < h; i++) {
    process.stdout.write("\x1b[1A");
    process.stdout.write("\x1b[2K");
  }
}

function draw(data: Pt[], w: number, h: number) {
  let count = 0;
  for (let i = 0; i < h; i++) {
    const points = data.filter(([x, y]) => y === i);
    for (const pt of points) {
      if (points.some((p) => p[0] === w - pt[0])) {
        count++;
      }
    }
  }

  if (count > 80) {
    for (let i = 0; i < h; i++) {
      const line = new Array(w).fill(" ");
      const points = data.filter(([x, y]) => y === i);
      for (const pt of points) {
        line[pt[0]] = "#";
      }
      process.stdout.write(line.join("") + "\n");
    }
    return true;
  } else {
  }
  return false;
}
