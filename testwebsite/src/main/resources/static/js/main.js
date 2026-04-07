// ================================
// MAIN.JS — Shared site JavaScript
// ================================

document.addEventListener('DOMContentLoaded', () => {

    // --- Navbar: scroll effect ---
    const navbar = document.getElementById('navbar');

    if (navbar) {
        const onScroll = () => {
            if (window.scrollY > 60) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        };
        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll(); // run once on load
    }

    // --- Navbar: mobile toggle ---
    const navToggle = document.getElementById('navToggle');

    if (navToggle && navbar) {
        navToggle.addEventListener('click', () => {
            navbar.classList.toggle('open');
            // Animate hamburger to X
            const spans = navToggle.querySelectorAll('span');
            if (navbar.classList.contains('open')) {
                spans[0].style.transform = 'rotate(45deg) translate(4px, 4px)';
                spans[1].style.opacity = '0';
                spans[2].style.transform = 'rotate(-45deg) translate(4px, -4px)';
            } else {
                spans[0].style.transform = '';
                spans[1].style.opacity = '';
                spans[2].style.transform = '';
            }
        });

        // Close nav when a link is clicked
        navbar.querySelectorAll('.navbar__links a').forEach(link => {
            link.addEventListener('click', () => {
                navbar.classList.remove('open');
                const spans = navToggle.querySelectorAll('span');
                spans[0].style.transform = '';
                spans[1].style.opacity = '';
                spans[2].style.transform = '';
            });
        });
    }

    // --- Quick Book: set default dates ---
    const checkIn  = document.getElementById('checkIn');
    const checkOut = document.getElementById('checkOut');

    if (checkIn && checkOut) {
        const today    = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);

        const fmt = (d) => d.toISOString().split('T')[0];

        checkIn.min   = fmt(today);
        checkIn.value = fmt(today);

        checkOut.min   = fmt(tomorrow);
        checkOut.value = fmt(tomorrow);

        checkIn.addEventListener('change', () => {
            const nextDay = new Date(checkIn.value);
            nextDay.setDate(nextDay.getDate() + 1);
            checkOut.min = fmt(nextDay);
            if (new Date(checkOut.value) <= new Date(checkIn.value)) {
                checkOut.value = fmt(nextDay);
            }
        });
    }

    // --- Scroll reveal (lightweight, no library needed) ---
    const revealEls = document.querySelectorAll(
        '.highlight-card, .stat, .testimonial__inner, .cta-banner__content'
    );

    if ('IntersectionObserver' in window && revealEls.length) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.15 });

        revealEls.forEach(el => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(20px)';
            el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(el);
        });
    }
});
