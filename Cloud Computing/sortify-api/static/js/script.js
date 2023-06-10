const nav = document.querySelector(".nav");
const nav2 = document.querySelector(".nav2");
document.querySelector("#hamburg").onclick = () => {
  nav.classList.toggle("active");
};
const hamburg = document.querySelector("#hamburg");

document.addEventListener("click", (e) => {
  if (!hamburg.contains(e.target) && !nav.contains(e.target)) {
    nav.classList.remove("active");
  }
});

const navbar = document.querySelector(".navbar");
const logo = document.querySelector(".logo");
window.onscroll = () => {
  let scrollTop = window.innerHeight;
  let scrollY = window.scrollY;

  if (scrollY >= scrollTop) {
    navbar.style.backgroundColor = "#ffffffb3";
    logo.style.color = "#000";
    nav.style.color = "#000";
    nav2.style.color = "#000";
  } else {
    navbar.style.backgroundColor = "#00000000";
    logo.style.color = "var(--cream)";
    nav.style.color = "var(--cream)";
    nav2.style.color = "var(--cream)";
  }
};
window.addEventListener(
  "contextmenu",
  (e) => {
    e.preventDefault();
  },
  false
);

