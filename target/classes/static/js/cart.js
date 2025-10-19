// Cart Management System - Database Backend Version
let cart = [];

// Get CSRF token from meta tag
function getCsrfToken() {
    const token = document.querySelector('meta[name="_csrf"]');
    return token ? token.getAttribute('content') : '';
}

function getCsrfHeader() {
    const header = document.querySelector('meta[name="_csrf_header"]');
    return header ? header.getAttribute('content') : 'X-CSRF-TOKEN';
}

// Initialize cart from backend
document.addEventListener('DOMContentLoaded', function() {
    loadCartFromBackend();
});

// Load cart from backend
async function loadCartFromBackend() {
    try {
        const response = await fetch('/cart/data', {
            headers: {
                [getCsrfHeader()]: getCsrfToken()
            }
        });

        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                cart = data.items || [];
                console.log('Cart loaded from backend:', cart.length, 'items');
                updateCartBadge();
            }
        } else {
            console.log('Not logged in or error loading cart');
            cart = [];
        }
    } catch (error) {
        console.error('Error loading cart:', error);
        cart = [];
    }
}

// Add item to cart
async function addToCart(productId, name, quantity, retailPrice, wholesalePrice, minWholesaleQty) {
    console.log('Adding to cart:', {productId, name, quantity});

    try {
        const formData = new URLSearchParams();
        formData.append('productId', productId);
        formData.append('quantity', quantity);

        const response = await fetch('/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [getCsrfHeader()]: getCsrfToken()
            },
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                cart = data.items || [];
                updateCartBadge();
                showNotification('✓ ' + name + ' added to cart!', 'success');
                console.log('Cart updated. Total items:', cart.length);
            } else {
                showNotification(data.message || 'Failed to add item', 'danger');
            }
        } else {
            showNotification('Failed to add item to cart', 'danger');
        }
    } catch (error) {
        console.error('Error adding to cart:', error);
        showNotification('Error adding to cart', 'danger');
    }
}

// Remove item from cart
async function removeFromCart(productId) {
    try {
        const response = await fetch(`/cart/remove/${productId}`, {
            method: 'POST',
            headers: {
                [getCsrfHeader()]: getCsrfToken()
            }
        });

        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                cart = data.items || [];
                updateCartBadge();
                return true;
            }
        }
    } catch (error) {
        console.error('Error removing from cart:', error);
    }
    return false;
}

// Update item quantity
async function updateQuantity(productId, newQuantity) {
    try {
        const formData = new URLSearchParams();
        formData.append('productId', productId);
        formData.append('quantity', newQuantity);

        const response = await fetch('/cart/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [getCsrfHeader()]: getCsrfToken()
            },
            body: formData
        });

        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                cart = data.items || [];
                updateCartBadge();
            }
        }
    } catch (error) {
        console.error('Error updating quantity:', error);
    }
}

// Update cart badge
function updateCartBadge() {
    const badges = document.querySelectorAll('#cart-badge, #cart-badge-nav, .cart-badge');
    badges.forEach(badge => {
        if (badge) {
            const totalItems = cart.length;
            if (totalItems > 0) {
                badge.textContent = totalItems;
                badge.style.display = 'inline-block';
            } else {
                badge.style.display = 'none';
            }
        }
    });
}

// Calculate cart total
function calculateTotal() {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
}

// Show notification
function showNotification(message, type = 'info') {
    const existingNotifications = document.querySelectorAll('.cart-notification');
    existingNotifications.forEach(n => n.remove());

    const notification = document.createElement('div');
    notification.className = `alert alert-${type} position-fixed top-0 start-50 translate-middle-x m-3 fade show cart-notification`;
    notification.style.zIndex = '9999';
    notification.style.minWidth = '300px';
    notification.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="bi bi-${type === 'success' ? 'check-circle-fill' : type === 'danger' ? 'exclamation-circle-fill' : 'info-circle-fill'} me-2"></i>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 150);
    }, 3000);
}

// Get cart data
function getCartData() {
    return cart;
}

// Clear cart
async function clearCart() {
    if (confirm('Are you sure you want to clear all items from the cart?')) {
        window.location.href = '/cart/clear';
    }
}

// Backward compatibility - does nothing, cart auto-saves
function saveCart() {
    console.log('saveCart() called - cart automatically saved to backend');
}

// Load cart on page load
loadCartFromBackend();
