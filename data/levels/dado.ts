export interface Challenge {
  emoji: string;
  text: string;
  category: "movimiento" | "mimica" | "sonidos" | "familia";
}

export const DADO_CATEGORIES: { id: Challenge["category"]; label: string; emoji: string }[] = [
  { id: "movimiento", label: "Movimiento", emoji: "🏃" },
  { id: "mimica", label: "Mímica", emoji: "🎭" },
  { id: "sonidos", label: "Sonidos", emoji: "🔊" },
  { id: "familia", label: "Familia", emoji: "👨‍👩‍👧" },
];

export const CHALLENGES: Challenge[] = [
  { emoji: "🐇", text: "Salta como conejo 3 veces", category: "movimiento" },
  { emoji: "🔄", text: "Gira como trompo 3 vueltas", category: "movimiento" },
  { emoji: "🐢", text: "Camina lento como tortuga", category: "movimiento" },
  { emoji: "🦵", text: "Salta en un solo pie", category: "movimiento" },
  { emoji: "🤸", text: "Estírate como un gato", category: "movimiento" },
  { emoji: "🕺", text: "Baila 10 segundos", category: "movimiento" },
  { emoji: "🐒", text: "Camina como changuito", category: "mimica" },
  { emoji: "🦁", text: "Ruge como león", category: "mimica" },
  { emoji: "🐘", text: "Camina como elefante", category: "mimica" },
  { emoji: "🦋", text: "Vuela como mariposa", category: "mimica" },
  { emoji: "🐍", text: "Muévete como serpiente", category: "mimica" },
  { emoji: "🤖", text: "Camina como robot", category: "mimica" },
  { emoji: "🐄", text: "Haz sonido de vaca", category: "sonidos" },
  { emoji: "🐱", text: "Haz sonido de gato", category: "sonidos" },
  { emoji: "🚗", text: "Haz sonido de carro", category: "sonidos" },
  { emoji: "🦆", text: "Haz sonido de pato", category: "sonidos" },
  { emoji: "🐝", text: "Haz sonido de abeja", category: "sonidos" },
  { emoji: "🚂", text: "Haz sonido de tren", category: "sonidos" },
  { emoji: "🤗", text: "Dale un abrazo a mamá o papá", category: "familia" },
  { emoji: "😘", text: "Dale un beso a alguien de tu familia", category: "familia" },
  { emoji: "👏", text: "Aplaude 5 veces con alguien", category: "familia" },
  { emoji: "🙌", text: "Choca las manos con toda la familia", category: "familia" },
  { emoji: "🥰", text: "Dile 'te quiero' a alguien", category: "familia" },
  { emoji: "🤝", text: "Ayuda a guardar un juguete", category: "familia" },
];
