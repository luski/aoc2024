import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

type Pt = [number, number];

const N = [
	[-1, 0],
	[1, 0],
	[0, -1],
	[0, 1],
];

const { board, w, h } = await readInput();
const [result1, result2] = solve();
console.log("Part 1:", result1);
console.log("Part 2:", result2);

async function readInput() {
	const rl = createInterface({ input, output });
	const board: number[][] = [];
	let w = 0,
		h = 0;

	for await (const line of rl) {
		if (line.trim() === "") break;
		w = line.length;
		board.push(line.split("").map(Number));
		h++;
	}
	rl.close();

	return { board, w, h };
}

function solve() {
	let result1 = 0;
	let result2 = 0;

	for (let i = 0; i < h; i++) {
		for (let j = 0; j < w; j++) {
			if (board[i][j] === 0) {
				const [d1, d2] = rec([i, j]);
				result1 += d1;
				result2 += d2;
			}
		}
	}

	return [result1, result2];
}

function rec(pt: Pt, result: Set<string> = new Set(), score = 0) {
	const [i, j] = pt;

	if (board[i][j] >= 9) {
		result.add(`${i},${j}`);
		return [result.size, score + 1];
	}

	for (const neighbor of next(pt)) {
		score += rec(neighbor, result)[1];
	}

	return [result.size, score];
}

function next([i, j]: Pt): Pt[] {
	const val = board[i][j];
	return N.map(([di, dj]): Pt => [i + di, j + dj]).filter(
		([i, j]) => i >= 0 && i < h && j >= 0 && j < w && val + 1 === board[i][j],
	);
}
