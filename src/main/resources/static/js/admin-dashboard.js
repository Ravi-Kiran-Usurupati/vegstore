// Admin Dashboard Charts
document.addEventListener('DOMContentLoaded', function() {
    loadSalesChart();
    loadProfitChart();
    loadPerformanceChart();
});

// Sales Trend Chart
async function loadSalesChart() {
    try {
        const response = await fetch('/admin/api/sales-trend?days=30');
        const data = await response.json();

        const labels = Object.keys(data);
        const values = Object.values(data);

        const ctx = document.getElementById('salesChart').getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Sales (₹)',
                    data: values,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Sales: ₹' + context.parsed.y.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '₹' + value;
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error loading sales chart:', error);
    }
}

async function loadProfitChart() {
    try {
        const response = await fetch('/admin/api/profit-analysis');
        const data = await response.json();

        const ctx = document.getElementById('profitChart').getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Revenue', 'COGS', 'Salary Expense', 'Profit'],
                datasets: [{
                    data: [data.revenue, data.cogs, data.salary, data.profit],
                    backgroundColor: [
                        'rgba(54, 162, 235, 0.8)',
                        'rgba(255, 99, 132, 0.8)',
                        'rgba(255, 206, 86, 0.8)', // Yellow for salary
                        'rgba(75, 192, 192, 0.8)'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ₹' + context.parsed.toFixed(2);
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error loading profit chart:', error);
    }
}
async function loadPerformanceChart() {
    try {
        const response = await fetch('/admin/api/salesperson-performance');
        const data = await response.json();

        const labels = Object.keys(data);
        const values = Object.values(data);

        const ctx = document.getElementById('performanceChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Completed Orders',
                    data: values,
                    backgroundColor: 'rgba(255, 122, 112, 0.3)',
                    borderColor: 'rgba(255, 122, 112, 0.4)',
                    borderWidth: 1,
                    barPercentage: 0.5,
                    categoryPercentage: 0.6,
                }]
            },
            options: {
                indexAxis: 'y', // makes horizontal bar chart for compact look
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.parsed.x}`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    },
                    y: {
                        ticks: { font: { size: 12 } }
                    }
                }
            }
        });

    } catch (error) {
        console.error('Error loading performance chart:', error);
    }
}

