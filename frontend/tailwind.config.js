/** @type {import('tailwindcss').Config} */
const primeui = require('tailwindcss-primeui');

module.exports = {
  darkMode: ['selector', '.app-dark'],
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        // Palette institutionnelle ENSI (vert foncé)
        ensi: {
          50: '#e9f5ef',
          100: '#c9e7d7',
          200: '#9fd3b9',
          300: '#6dba96',
          400: '#3f9e74',
          500: '#1f7d56',
          600: '#0b5d3b',
          700: '#0a4d31',
          800: '#083d27',
          900: '#06301f',
          950: '#031a11'
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif']
      }
    }
  },
  plugins: [primeui]
};
