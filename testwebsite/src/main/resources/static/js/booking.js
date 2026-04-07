// ================================
// BOOKING.JS — Live summary + validation
// ================================

document.addEventListener('DOMContentLoaded', () => {

    // --- Element refs ---
    const roomOptions    = document.querySelectorAll('.room-option');
    const checkInInput   = document.getElementById('checkIn');
    const checkOutInput  = document.getElementById('checkOut');
    const guestSelect    = document.getElementById('guestCount');
    const summaryDetails = document.getElementById('summaryDetails');
    const summaryRoom    = document.getElementById('summaryRoom');
    const summaryRoomName = document.getElementById('summaryRoomName');
    const summaryCheckIn  = document.getElementById('summaryCheckIn');
    const summaryCheckOut = document.getElementById('summaryCheckOut');
    const summaryNights   = document.getElementById('summaryNights');
    const summaryGuests   = document.getElementById('summaryGuests');
    const summaryTotal    = document.getElementById('summaryTotal');

    // Room price map — read from data attributes on room options
    let selectedRoom = null;

    // --- Set default dates ---
    const today    = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    const fmt = d => d.toISOString().split('T')[0];

    if (checkInInput && checkOutInput) {
        checkInInput.min   = fmt(today);
        checkInInput.value = fmt(today);
        checkOutInput.min   = fmt(tomorrow);
        checkOutInput.value = fmt(tomorrow);
    }

    // --- Room selection ---
    roomOptions.forEach(option => {
        option.addEventListener('click', () => {
            // Update visual selection
            roomOptions.forEach(o => o.classList.remove('selected'));
            option.classList.add('selected');

            // Read room data from the label's child elements
            const name  = option.querySelector('strong')?.textContent?.trim() || '—';
            const price = parseFloat(
                option.querySelector('.room-option__price')
                    ?.textContent?.replace(/[^0-9.]/g, '') || '0'
            );

            selectedRoom = { name, price };
            updateSummary();
        });

        // Also handle when the radio changes via keyboard
        const radio = option.querySelector('input[type="radio"]');
        if (radio?.checked) {
            option.classList.add('selected');
            const name  = option.querySelector('strong')?.textContent?.trim() || '—';
            const price = parseFloat(
                option.querySelector('.room-option__price')
                    ?.textContent?.replace(/[^0-9.]/g, '') || '0'
            );
            selectedRoom = { name, price };
        }
    });

    // --- Date changes ---
    if (checkInInput) {
        checkInInput.addEventListener('change', () => {
            const next = new Date(checkInInput.value);
            next.setDate(next.getDate() + 1);
            checkOutInput.min = fmt(next);
            if (new Date(checkOutInput.value) <= new Date(checkInInput.value)) {
                checkOutInput.value = fmt(next);
            }
            updateSummary();
        });
    }

    if (checkOutInput) {
        checkOutInput.addEventListener('change', updateSummary);
    }

    if (guestSelect) {
        guestSelect.addEventListener('change', updateSummary);
    }

    // --- Update summary card ---
    function updateSummary() {
        if (!selectedRoom || !checkInInput?.value || !checkOutInput?.value) return;

        const checkIn  = new Date(checkInInput.value);
        const checkOut = new Date(checkOutInput.value);
        const nights   = Math.max(0, Math.round((checkOut - checkIn) / (1000 * 60 * 60 * 24)));

        if (nights <= 0) return;

        const total    = selectedRoom.price * nights;
        const guests   = guestSelect?.value || '—';

        summaryRoomName.textContent  = selectedRoom.name;
        summaryCheckIn.textContent   = formatDate(checkIn);
        summaryCheckOut.textContent  = formatDate(checkOut);
        summaryNights.textContent    = nights + (nights === 1 ? ' night' : ' nights');
        summaryGuests.textContent    = guests + (parseInt(guests) === 1 ? ' guest' : ' guests');
        summaryTotal.textContent     = '$' + total.toLocaleString('en-US', { maximumFractionDigits: 0 });

        // Show the details panel
        if (summaryDetails) summaryDetails.style.display = 'block';
        if (summaryRoom) {
            const placeholder = summaryRoom.querySelector('.booking-summary__placeholder');
            if (placeholder) placeholder.style.display = 'none';
        }
    }

    function formatDate(date) {
        return date.toLocaleDateString('en-US', { day: '2-digit', month: 'short', year: 'numeric' });
    }

    // --- Form validation before submit ---
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        bookingForm.addEventListener('submit', (e) => {
            const roomSelected = document.querySelector('input[name="roomId"]:checked');
            if (!roomSelected) {
                e.preventDefault();
                alert('Please select a room before continuing.');
                return;
            }

            const checkIn  = new Date(checkInInput.value);
            const checkOut = new Date(checkOutInput.value);
            if (checkOut <= checkIn) {
                e.preventDefault();
                alert('Check-out date must be after check-in date.');
            }
        });
    }

    // --- Trigger initial summary if a room is pre-selected ---
    updateSummary();
});
