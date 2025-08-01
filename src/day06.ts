import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Direction = "up" | "down" | "left" | "right";

type StepData = {
  pt: [number, number];
  direction: Direction;
};

let { board, startingPoint } = await readInput();
console.log("Part 1:", solution1()?.size);
console.log("Part 2:", solution2());

async function readInput() {
  const rl = createInterface({ input, output });

  const board: string[][] = [];

  let startingPoint: [number, number] = [0, 0];
  let currentLine = 0;

  for await (const line of rl) {
    if (line.trim() === "") break;

    board.push(
      line.split("").map((char, index) => {
        switch (char) {
          case "^":
            startingPoint = [currentLine, index];
            return ".";

          default:
            return char;
        }
      }),
    );

    currentLine++;
  }
  rl.close();

  return { board, startingPoint };
}

function solution1() {
  let direction: Direction = "up";
  let pt = startingPoint;

  let currentField = board[pt[0]][pt[1]];
  const stepsHistory = new Set<string>();
  stepsHistory.add(`${direction}|${pt[0]}|${pt[1]}`);

  while (currentField === ".") {
    const step = go(direction, pt);
    if (!step) {
      break;
    } else {
      pt = step.pt;
      direction = step.direction;
      const stepHash = `${direction}|${pt[0]}|${pt[1]}`;
      if (stepsHistory.has(stepHash)) {
        return null;
      } else {
        stepsHistory.add(stepHash);
      }
    }
  }

  return new Set(
    [...stepsHistory].map((step) => step.split("|").slice(1).join("|")),
  );
}

function solution2() {
  let result = 0;
  const visitedPoints = solution1()!;

  for (const pointHash of visitedPoints) {
    const [i, j] = pointHash.split("|").map(Number);
    if (i === startingPoint[0] && j === startingPoint[1]) {
      continue;
    }
    board[i][j] = "#";
    if (solution1() === null) {
      result += 1;
    }
    board[i][j] = ".";
  }

  return result;
}

function go(direction: Direction, pt: [number, number]): StepData | null {
  let nextPt: [number, number];
  let nextDirection: Direction;

  switch (direction) {
    case "up":
      nextPt = [pt[0] - 1, pt[1]];
      nextDirection = "right";
      break;
    case "down":
      nextPt = [pt[0] + 1, pt[1]];
      nextDirection = "left";
      break;
    case "left":
      nextPt = [pt[0], pt[1] - 1];
      nextDirection = "up";
      break;
    case "right":
      nextPt = [pt[0], pt[1] + 1];
      nextDirection = "down";
      break;
  }

  if (
    nextPt[0] < 0 ||
    nextPt[1] < 0 ||
    nextPt[0] >= board.length ||
    nextPt[1] >= board[0].length
  ) {
    return null;
  }

  if (board[nextPt[0]][nextPt[1]] === "#") {
    return { pt, direction: nextDirection };
  }
  return { pt: nextPt, direction };
}
