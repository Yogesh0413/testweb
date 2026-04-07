// ================================
// GALLERY.JS — Filter + Lightbox
// ================================

document.addEventListener('DOMContentLoaded', () => {

    const items     = Array.from(document.querySelectorAll('.gallery-item'));
    const tabs      = document.querySelectorAll('.gallery-tab');
    const lightbox  = document.getElementById('lightbox');
    const lbImg     = document.getElementById('lightboxImg');
    const lbCaption = document.getElementById('lightboxCaption');
    const lbClose   = document.getElementById('lightboxClose');
    const lbPrev    = document.getElementById('lightboxPrev');
    const lbNext    = document.getElementById('lightboxNext');

    let visibleItems = [...items];
    let currentIndex = 0;

    // ---- Filter Tabs ----
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            const filter = tab.dataset.filter;

            items.forEach(item => {
                const match = filter === 'all' || item.dataset.category === filter;
                item.classList.toggle('hidden', !match);
            });

            visibleItems = items.filter(item => !item.classList.contains('hidden'));
        });
    });

    // ---- Open Lightbox ----
    items.forEach(item => {
        item.addEventListener('click', () => {
            const src     = item.dataset.src;
            const caption = item.dataset.caption;

            currentIndex = visibleItems.indexOf(item);
            openLightbox(src, caption);
        });
    });

    function openLightbox(src, caption) {
        lbImg.classList.add('loading');
        lightbox.classList.add('open');
        lightbox.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';

        lbCaption.textContent = caption || '';

        const tempImg = new Image();
        tempImg.onload = () => {
            lbImg.src = src;
            lbImg.classList.remove('loading');
        };
        tempImg.src = src;
    }

    function closeLightbox() {
        lightbox.classList.remove('open');
        lightbox.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
        lbImg.src = '';
    }

    function showIndex(index) {
        currentIndex = (index + visibleItems.length) % visibleItems.length;
        const item = visibleItems[currentIndex];
        openLightbox(item.dataset.src, item.dataset.caption);
    }

    // ---- Controls ----
    lbClose.addEventListener('click', closeLightbox);
    lbPrev.addEventListener('click', () => showIndex(currentIndex - 1));
    lbNext.addEventListener('click', () => showIndex(currentIndex + 1));

    // Click outside image to close
    lightbox.addEventListener('click', (e) => {
        if (e.target === lightbox) closeLightbox();
    });

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
        if (!lightbox.classList.contains('open')) return;
        if (e.key === 'Escape')      closeLightbox();
        if (e.key === 'ArrowRight')  showIndex(currentIndex + 1);
        if (e.key === 'ArrowLeft')   showIndex(currentIndex - 1);
    });
});
