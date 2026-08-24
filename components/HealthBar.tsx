"use client";

export default function HealthBar({ hp, max = 100 }: { hp: number; max?: number }) {
  const pct = Math.max(0, Math.min(100, (hp / max) * 100));
  const color = pct > 50 ? "bg-emerald-500" : pct > 20 ? "bg-amber-500" : "bg-rose-500";
  return (
    <div className="h-5 w-56 overflow-hidden rounded-full bg-slate-200 shadow-inner">
      <div
        className={`h-full ${color} transition-all duration-300`}
        style={{ width: `${pct}%` }}
      />
    </div>
  );
}
