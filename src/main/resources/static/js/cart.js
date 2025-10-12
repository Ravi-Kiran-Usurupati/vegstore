/**
 * GreenBasket Cart Management
 * Handles client-side cart operations with local storage
 * Includes dynamic pricing logic for wholesale customers
 */

// Get cart from local storage
function getCart() {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : {};
}

// Save cart to local storage
function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
}

// Calculate the correct price based on customer type and quantity
function calculatePrice(quantity, retailPrice, wholesalePrice, minWholesaleQty) {
    const isWholesale = localStorage.getItem('isWholesale') === 'true';

    if (isWholesale && quantity >= minWholesaleQty) {
        return wholesalePrice;
    }
    return retailPrice;
}

// Add item to cart
function addToCart(productId, name, quantity, retailPrice, wholesalePrice, minWholesaleQty) {
    quantity = parseFloat(quantity);

    if (quantity <= 0) {
        alert('Please enter a valid quantity');
        return;
    }

    const cart = getCart();

    // Calculate the appropriate price
    const price = calculatePrice(quantity, retailPrice, wholesalePrice, minWholesaleQty);

    // If item exists, update quantity and recalculate price
    if (cart[productId]) {
        const newQuantity = cart[productId].quantity + quantity;
        const newPrice = calculatePrice(newQuantity, retailPrice, wholesalePrice, minWholesaleQty);
        cart[productId].quantity = newQuantity;
        cart[productId].price = newPrice;
    } else {
        cart[productId] = {
            name: name,
            quantity: quantity,
            price: price,
            retailPrice: retailPrice,
            wholesalePrice: wholesalePrice,
            minWholesaleQty: minWholesaleQty
        };
    }

    saveCart(cart);
    alert(`${name} added to cart!`);
    updateCartBadge();
}

// Update cart item quantity
function updateCartQuantity(productId, newQuantity) {
    newQuantity = parseFloat(newQuantity);

    if (newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }

    const cart = getCart();

    if (cart[productId]) {
        const item = cart[productId];
        // Recalculate price based on new quantity
        const newPrice = calculatePrice(
            newQuantity,
            item.retailPrice,
            item.wholesalePrice,
            item.minWholesaleQty
        );

        cart[productId].quantity = newQuantity;
        cart[productId].price = newPrice;

        saveCart(cart);
        displayCart();
    }
}

// Remove item from cart
function removeFromCart(productId) {
    const cart = getCart();
    delete cart[productId];
    saveCart(cart);
    displayCart();
}

// Clear entire cart
function clearCart() {
    if (confirm('Are you sure you want to clear your cart?')) {
        localStorage.removeItem('cart');
        displayCart();
    }
}

// Display cart items on cart page
function displayCart() {
    const cart = getCart();
    const cartItemsBody = document.getElementById('cart-items-body');
    const emptyCart = document.getElementById('empty-cart');
    const cartItems = document.getElementById('cart-items');

    if (!cartItemsBody) return; // Not on cart page

    if (Object.keys(cart).length === 0) {
        emptyCart.style.display = 'block';
        cartItems.style.display = 'none';
        return;
    }

    emptyCart.style.display = 'none';
    cartItems.style.display = 'block';

    let html = '';
    let total = 0;

    for (const [id, item] of Object.entries(cart)) {
        const subtotal = item.price * item.quantity;
        total += subtotal;

        html += `
            <tr>
                <td>${item.name}</td>
                <td>₹${item.price.toFixed(2)}</td>
                <td>
                    <input type="number" class="form-control" style="width: 100px;"
                           value="${item.quantity}" min="0.5" step="0.5"
                           onchange="updateCartQuantity(${id}, this.value)">
                </td>
                <td>₹${subtotal.toFixed(2)}</td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="removeFromCart(${id})">
                        <i class="bi bi-trash"></i> Remove
                    </button>
                </td>
            </tr>
        `;
    }

    cartItemsBody.innerHTML = html;
    document.getElementById('cart-total').textContent = total.toFixed(2);
    updateCartBadge();
}

// Update cart item count badge
function updateCartBadge() {
    const cart = getCart();
    const itemCount = Object.keys(cart).length;

    // Update badge if it exists in navbar
    const badge = document.getElementById('cart-badge');
    if (badge) {
        badge.textContent = itemCount;
        badge.style.display = itemCount > 0 ? 'inline' : 'none';
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updateCartBadge();
});
