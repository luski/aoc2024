import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Pt = [number, number];

function matrix<T>(h: number, w: number, value: T) {
  return new Array(h).fill(0).map(() => new Array(w).fill(value));
}

const board = await readInput();
const H = board.length;
const W = board[0].length;
const N = [
  [-1, 0],
  [0, 1],
  [1, 0],
  [0, -1],
];
const V = [
  [-1, -1],
  [-1, 0],
  [0, 0],
  [0, -1],
];

console.log("Part 1:", solve(1));
console.log("Part 2:", solve(2));

function solve(part: 1 | 2) {
  const visited: boolean[][] = matrix(H, W, false);
  let result = 0;

  for (let i = 0; i < H; i++) {
    for (let j = 0; j < W; j++) {
      if (!visited[i][j]) {
        const { area, score, vertices } = visit(i, j, visited);
        if (part === 1) {
          result += area * score;
        } else {
          result += area * vertices;
        }
      }
    }
  }
  return result;
}

function visit(i: number, j: number, visited: boolean[][]) {
  let queue: Pt[] = [[i, j]];
  let area = 0;
  let score = 0;
  const verticesMap = matrix(H + 1, W + 1, 0);

  while (queue.length) {
    const [ni, nj] = queue.shift()!;
    if (visited[ni][nj]) continue;
    visited[ni][nj] = true;
    verticesMap[ni][nj] = 1;
    area += 1;
    score += calcScore(ni, nj);
    queue.push(...unvisitedNeighbors(ni, nj, visited));
  }

  return { area, score, vertices: calcVertices(verticesMap) };
}

function unvisitedNeighbors(i: number, j: number, visited: boolean[][]): Pt[] {
  return N.map(([di, dj]) => [di + i, dj + j] as Pt).filter(
    ([ni, nj]) =>
      ni >= 0 &&
      nj >= 0 &&
      ni < H &&
      nj < W &&
      !visited[ni][nj] &&
      board[i][j] === board[ni][nj],
  );
}

function calcScore(i: number, j: number) {
  return (
    4 -
    N.filter(([di, dj]) => {
      const [ni, nj] = [i + di, j + dj];
      return (
        ni >= 0 && nj >= 0 && ni < H && nj < W && board[i][j] === board[ni][nj]
      );
    }).length
  );
}

function getVertValue(i: number, j: number, verticesMap: number[][]) {
  return i < 0 || j < 0 || i >= H || j >= W ? 0 : verticesMap[i][j];
}

function calcVertices(verticesMap: number[][]) {
  let result = 0;
  for (let i = 0; i < H + 1; i++) {
    for (let j = 0; j < W + 1; j++) {
      result += scanVertices(i, j, verticesMap);
    }
  }
  return result;
}
function scanVertices(i: number, j: number, verticesMap: number[][]) {
  const [a, b, c, d] = V.map(([di, dj]) =>
    getVertValue(i + di, j + dj, verticesMap),
  );
  if (!a && b && c && d) return 1;
  if (a && !b && c && d) return 1;
  if (a && b && !c && d) return 1;
  if (a && b && c && !d) return 1;
  if (!a && !b && !c && d) return 1;
  if (!a && !b && c && !d) return 1;
  if (!a && b && !c && !d) return 1;
  if (a && !b && !c && !d) return 1;
  if (a && !b && c && !d) return 2;
  if (!a && b && !c && d) return 2;
  return 0;
}

async function readInput() {
  const rl = createInterface({ input, output });
  const board: string[][] = [];
  let result: string[] = [];
  for await (const line of rl) {
    if (line.trim() === "") break;
    board.push(line.split(""));
  }
  rl.close();

  return board;
}
