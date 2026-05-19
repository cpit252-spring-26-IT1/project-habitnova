/**
 * HabitNova — Frontend JavaScript
 *
 * All user interactions (add, complete, delete, reminder, priority) are
 * sent as AJAX calls to the REST API (/api/habits) and the page reloads
 * to show updated data from the server-rendered Thymeleaf template.
 */
const API = '/api/habits';

function toast(message, isError) {
    const el = document.createElement('div');
    el.className = 'toast' + (isError ? ' toast--error' : '');
    el.textContent = message;
    document.body.appendChild(el);
    setTimeout(() => el.remove(), 3000);
}

function reload() { window.location.reload(); }

// ── Add Habit (Factory Method on the server) ──
document.getElementById('add-btn')?.addEventListener('click', async () => {
    const category    = document.getElementById('category').value;
    const name        = document.getElementById('habit-name').value.trim();
    const description = document.getElementById('habit-desc').value.trim();

    if (!category || !name || !description) {
        toast('Please fill in all fields', true);
        return;
    }

    try {
        const res = await fetch(API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ category, name, description })
        });
        if (res.ok) {
            toast('Habit created!');
            setTimeout(reload, 400);
        } else {
            toast('Failed to create habit', true);
        }
    } catch (e) {
        toast('Network error', true);
    }
});

// ── Complete / Uncomplete (triggers Observer events) ──
document.querySelectorAll('.check-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
        const id     = btn.dataset.id;
        const isDone = btn.classList.contains('check-btn--done');
        const url    = isDone ? `${API}/${id}/uncomplete` : `${API}/${id}/complete`;

        const res = await fetch(url, { method: 'POST' });
        if (res.ok) {
            toast(isDone ? 'Unmarked' : 'Completed!');
            setTimeout(reload, 400);
        }
    });
});

// ── Delete ──
document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
        if (!confirm('Delete this habit?')) return;
        const res = await fetch(`${API}/${btn.dataset.id}`, { method: 'DELETE' });
        if (res.ok) {
            toast('Habit deleted');
            setTimeout(reload, 400);
        }
    });
});

// ── Reminder (Decorator Pattern) ──
document.querySelectorAll('.reminder-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
        const time = prompt('Set reminder time (HH:MM):', '07:00');
        if (!time) return;
        const res = await fetch(
            `${API}/${btn.dataset.id}/reminder?time=${encodeURIComponent(time)}`,
            { method: 'POST' }
        );
        if (res.ok) {
            toast('Reminder set for ' + time);
            setTimeout(reload, 400);
        }
    });
});

// ── Priority (Decorator Pattern) ──
document.querySelectorAll('.priority-btn').forEach(btn => {
    btn.addEventListener('click', async () => {
        const level = prompt('Set priority (HIGH, MEDIUM, LOW):', 'HIGH');
        if (!level) return;
        const res = await fetch(
            `${API}/${btn.dataset.id}/priority?level=${encodeURIComponent(level)}`,
            { method: 'POST' }
        );
        if (res.ok) {
            toast('Priority set to ' + level.toUpperCase());
            setTimeout(reload, 400);
        } else {
            toast('Invalid priority level', true);
        }
    });
});
