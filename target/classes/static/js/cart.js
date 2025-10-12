// Cart Management System
let cart = [];

// Initialize cart from localStorage
document.addEventListener('DOMContentLoaded', function() {
    loadCart();
    updateCartBadge();
});

// Load cart from localStorage
function loadCart() {
    const savedCart = localStorage.getItem('greenbasket_cart');
    if (savedCart) {
        try {
            cart = JSON.parse(savedCart);
        } catch (e) {
            console.error('Error parsing cart:', e);
            cart = [];
        }
    }
}

// Save cart to localStorage
function saveCart() {
    localStorage.setItem('greenbasket_cart', JSON.stringify(cart));
    updateCartBadge();
}

// Add item to cart
function addToCart(productId, name, quantity, retailPrice, wholesalePrice, minWholesaleQty) {
    console.log('Adding to cart:', {productId, name, quantity, retailPrice, wholesalePrice, minWholesaleQty});

    const isWholesale = localStorage.getItem('isWholesale') === 'true';

    // Calculate price based on customer type and quantity
    let price = parseFloat(retailPrice);
    if (isWholesale && quantity >= parseFloat(minWholesaleQty)) {
        price = parseFloat(wholesalePrice);
    }

    // Check if item already exists in cart
    const existingItemIndex = cart.findIndex(item => item.productId === productId);

    if (existingItemIndex > -1) {
        // Update existing item
        cart[existingItemIndex].quantity = parseFloat(cart[existingItemIndex].quantity) + parseFloat(quantity);
        cart[existingItemIndex].price = price;
        console.log('Updated existing item:', cart[existingItemIndex]);
    } else {
        // Add new item
        const newItem = {
            productId: parseInt(productId),
            name: name,
            quantity: parseFloat(quantity),
            retailPrice: parseFloat(retailPrice),
            wholesalePrice: parseFloat(wholesalePrice),
            minWholesaleQty: parseFloat(minWholesaleQty),
            price: price
        };
        cart.push(newItem);
        console.log('Added new item:', newItem);
    }

    saveCart();
    console.log('Cart saved. Total items:', cart.length);
    showNotification('✓ ' + name + ' added to cart!', 'success');
}

// Remove item from cart
function removeFromCart(productId) {
    const initialLength = cart.length;
    cart = cart.filter(item => item.productId !== productId);
    if (cart.length < initialLength) {
        saveCart();
        console.log('Item removed from cart');
        return true;
    }
    return false;
}

// Update item quantity
function updateQuantity(productId, newQuantity) {
    const item = cart.find(item => item.productId === productId);
    if (item) {
        item.quantity = parseFloat(newQuantity);

        // Recalculate price based on new quantity
        const isWholesale = localStorage.getItem('isWholesale') === 'true';
        if (isWholesale && item.quantity >= item.minWholesaleQty) {
            item.price = item.wholesalePrice;
        } else {
            item.price = item.retailPrice;
        }

        saveCart();
        console.log('Quantity updated:', item);
    }
}

// Update cart badge
function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (badge) {
        const totalItems = cart.length;
        console.log('Updating badge. Total items:', totalItems);
        if (totalItems > 0) {
            badge.textContent = totalItems;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }
}

// Calculate cart total
function calculateTotal() {
    return cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
}

// Show notification
function showNotification(message, type = 'info') {
    // Remove any existing notifications
    const existingNotifications = document.querySelectorAll('.cart-notification');
    existingNotifications.forEach(n => n.remove());

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} position-fixed top-0 start-50 translate-middle-x m-3 fade show cart-notification`;
    notification.style.zIndex = '9999';
    notification.style.minWidth = '300px';
    notification.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="bi bi-${type === 'success' ? 'check-circle-fill' : 'info-circle-fill'} me-2"></i>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(notification);

    // Remove after 3 seconds
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
function clearCart() {
    cart = [];
    saveCart();
    console.log('Cart cleared');
}

// Initialize cart on page load
loadCart();
