import { CATEGORY_INFO, CATEGORY_ORDER, gamesByCategory } from "@/data/games";
import GameCard from "@/components/GameCard";
import AnimatedBackground from "@/components/AnimatedBackground";
import Mascot from "@/components/Mascot";

export default function Home() {
  return (
    <div className="relative min-h-full flex-1 bg-gradient-to-b from-amber-50 via-orange-50 to-white">
      <AnimatedBackground />
      <main className="relative mx-auto w-full max-w-5xl px-4 py-8 sm:px-6">
        <div className="mb-2 flex items-center justify-center gap-3">
          <Mascot size={56} />
          <h1 className="text-center text-3xl font-extrabold text-amber-700 sm:text-4xl">
            Mundo de Juegos
          </h1>
        </div>
        <p className="mb-10 text-center text-base text-amber-800/70 sm:text-lg">
          Elige un juego para aprender jugando
        </p>

        {CATEGORY_ORDER.map((category) => {
          const list = gamesByCategory(category);
          const info = CATEGORY_INFO[category];
          return (
            <section key={category} className="mb-12">
              <h2 className="mb-4 flex items-center gap-2 text-xl font-extrabold text-amber-900 sm:text-2xl">
                <span>{info.emoji}</span> {info.label}
              </h2>
              <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 sm:gap-6 md:grid-cols-4">
                {list.map((game) => (
                  <GameCard key={game.id} game={game} />
                ))}
              </div>
            </section>
          );
        })}
      </main>
    </div>
  );
}
