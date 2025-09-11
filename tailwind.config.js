/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/templates/fragments/**/*.html",
    "./src/main/resources/static/js/**/*.js"
  ],
  theme: {
    extend: {
      colors: {
        brand: "#2e3f52",
        destaque: "#facc15"
      },
      borderRadius: {
        '2xl': '1rem'
      }
    }
  },
  plugins: []
}


