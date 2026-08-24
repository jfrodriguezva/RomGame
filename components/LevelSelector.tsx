"use client";

export default function LevelSelector({
  levels,
  active,
  onSelect,
}: {
  levels: number[];
  active: number;
  onSelect: (level: number) => void;
}) {
  return (
    <div className="flex flex-wrap justify-center gap-2">
      {levels.map((level) => (
        <button
          key={level}
          onClick={() => onSelect(level)}
          className={`h-11 w-11 rounded-full text-lg font-bold shadow transition active:scale-90 ${
            level === active
              ? "bg-indigo-600 text-white"
              : "bg-white text-indigo-600"
          }`}
        >
          {level}
        </button>
      ))}
    </div>
  );
}
