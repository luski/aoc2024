import { createInterface } from "readline/promises";
import { stdin as input, stdout as output } from "node:process";
import { cloneDeep } from "es-toolkit";

type Segment = {
  free: number;
  blocks: Block[];
};

type Block = [number, number];

const segmentsOriginal = await readInput();
let segments = cloneDeep(segmentsOriginal);
console.log("Part 1:", solution1());
segments = cloneDeep(segmentsOriginal);
console.log("Part 2:", solution2());

async function readInput() {
  const rl = createInterface({ input, output });
  const segments: Segment[] = [];

  for await (const line of rl) {
    if (line.trim() === "") break;
    for (let i = 0; i < line.length; i++) {
      if (i % 2 === 0) {
        segments.push({
          free: 0,
          blocks: [[Math.floor(i / 2), Number(line.charAt(i))]],
        });
      } else {
        segments.push({ free: Number(line.charAt(i)), blocks: [] });
      }
    }
  }
  rl.close();

  return segments;
}

function solution1() {
  let i = 1;
  let j =
    segments.at(-1)?.free === 0 ? segments.length - 1 : segments.length - 2;

  while (i < j && i < segments.length && j >= 0) {
    const target = segments[i];
    const source = segments[j];
    const [id, size] = source.blocks[0];

    const occupied = Math.min(target.free, size);
    target.blocks.push([id, occupied]);
    target.free -= occupied;
    source.blocks[0][1] -= occupied;
    source.free += occupied;
    if (source.blocks[0][1] === 0) {
      source.blocks = [];
      j -= 2;
    }
    if (target.free === 0) {
      i += 2;
    }
  }

  return calculateChecksum();
}

function solution2() {
  const lastIndex =
    segments.at(-1)?.free === 0 ? segments.length - 1 : segments.length - 2;

  for (let j = lastIndex; j >= 0; j -= 2) {
    for (let i = 1; i < j; i += 2) {
      const target = segments[i];
      const source = segments[j];

      const [id, size] = source.blocks[0];
      if (target.free >= size) {
        const occupied = Math.min(target.free, size);
        target.blocks.push([id, occupied]);
        target.free -= occupied;
        source.free += occupied;
        source.blocks = [];
        break;
      }
    }
  }
  return calculateChecksum();
}

function calculateChecksum() {
  const flattenSegments = segments.flatMap((s) => [...s.blocks, [0, s.free]]);

  return flattenSegments.reduce(
    ([counter, sum], [id, size]) => [
      counter + size,
      sum + (id * ((counter + counter + size - 1) * size)) / 2,
    ],
    [0, 0],
  )[1];
}
