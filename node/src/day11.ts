import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";

const cache = new Map<string, Map<number, number>>();
function setCacheVal(stone: string, steps: number, value: number) {
	if (!cache.has(stone)) {
		cache.set(stone, new Map());
	}
	cache.get(stone)!.set(steps, value);
}

function getCacheVal(stone: string, steps: number): number | undefined {
	if (cache.has(stone)) {
		return cache.get(stone)!.get(steps);
	}
	return undefined;
}

const stones = await readInput();
console.log("Part 1:", solve(stones, 25));
console.log("Part 2:", solve(stones, 75));

async function readInput() {
	const rl = createInterface({ input, output });
	let result: string[] = [];
	for await (const line of rl) {
		if (line.trim() === "") break;
		result = line.split(" ");
	}
	rl.close();

	return result;
}

function solve(stones: string[], steps: number) {
	let result = 0;
	for (const stone of stones) {
		result += rec(stone, steps);
	}
	return result;
}

function transformStone(stone: string): string[] {
	let result: string[];

	if (stone === "0") {
		result = ["1"];
	} else if (stone.length % 2 === 0) {
		result = [
			stone.slice(0, Math.floor(stone.length / 2)),
			Number(stone.slice(Math.floor(stone.length / 2))).toString(),
		];
	} else {
		result = [`${Number(stone) * 2024}`];
	}

	return result;
}

function rec(stone: string, steps: number) {
	if (steps === 0) {
		return 1;
	}
	const cached = getCacheVal(stone, steps);
	if (cached) {
		return cached;
	}

	let result = 0;
	for (const nextStone of transformStone(stone)) {
		result += rec(nextStone, steps - 1);
	}

	setCacheVal(stone, steps, result);
	return result;
}
