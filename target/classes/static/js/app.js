/**
 * Personal Expense & Budget Tracker - Frontend Application Script
 * Integrates Chart.js, Sidebar interactions, Modal data population, and AJAX polymorphic inspector.
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Sidebar Toggle Logic
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const mobileSidebarToggleBtn = document.getElementById('mobileSidebarToggleBtn');

    if (sidebarToggleBtn) {
        sidebarToggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded');
            // Store preference in localStorage
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebar_collapsed', isCollapsed);
        });
    }

    if (mobileSidebarToggleBtn) {
        mobileSidebarToggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('mobile-open');
        });
    }

    // Restore desktop preference
    if (window.innerWidth > 992 && localStorage.getItem('sidebar_collapsed') === 'true') {
        sidebar?.classList.add('collapsed');
        mainContent?.classList.add('expanded');
    }

    // 2. Auto-dismiss alerts
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 6000);
    });

    // 3. Edit Income Modal Handler
    const editIncomeModal = document.getElementById('editIncomeModal');
    if (editIncomeModal) {
        editIncomeModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('editIncomeId').value = button.getAttribute('data-id');
            document.getElementById('editIncomeTitle').value = button.getAttribute('data-title');
            document.getElementById('editIncomeAmount').value = button.getAttribute('data-amount');
            document.getElementById('editIncomeCategory').value = button.getAttribute('data-category');
            document.getElementById('editIncomeDate').value = button.getAttribute('data-date');
            document.getElementById('editIncomePaymentMethod').value = button.getAttribute('data-payment');
            document.getElementById('editIncomeDescription').value = button.getAttribute('data-description') || '';
        });
    }

    // 4. Edit Expense Modal Handler
    const editExpenseModal = document.getElementById('editExpenseModal');
    if (editExpenseModal) {
        editExpenseModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('editExpenseId').value = button.getAttribute('data-id');
            document.getElementById('editExpenseTitle').value = button.getAttribute('data-title');
            document.getElementById('editExpenseAmount').value = button.getAttribute('data-amount');
            document.getElementById('editExpenseCategory').value = button.getAttribute('data-category');
            document.getElementById('editExpenseDate').value = button.getAttribute('data-date');
            document.getElementById('editExpensePaymentMethod').value = button.getAttribute('data-payment');
            document.getElementById('editExpenseDescription').value = button.getAttribute('data-description') || '';
        });
    }

    // 5. Polymorphic Details Modal Handler (Section 10.G Dynamic Method Dispatch)
    const detailsModal = document.getElementById('transactionDetailsModal');
    if (detailsModal) {
        detailsModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const txId = button.getAttribute('data-id');
            const detailsContent = document.getElementById('polymorphicDetailsContent');
            detailsContent.textContent = 'Loading polymorphic details via dynamic method dispatch...';

            fetch('/transactions/details/' + txId)
                .then(response => response.text())
                .then(data => {
                    detailsContent.textContent = data;
                })
                .catch(err => {
                    detailsContent.textContent = 'Failed to load details: ' + err;
                });
        });
    }
});

// =========================================================================
// CHART.JS FACTORY FUNCTIONS
// =========================================================================

/**
 * 1. Bar Chart: Monthly Income vs Expenses
 */
function renderIncomeVsExpenseChart(canvasId, labels, incomeData, expenseData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Income (₹)',
                    data: incomeData,
                    backgroundColor: '#22C55E',
                    borderRadius: 6,
                    barPercentage: 0.6,
                    categoryPercentage: 0.7
                },
                {
                    label: 'Expenses (₹)',
                    data: expenseData,
                    backgroundColor: '#EF4444',
                    borderRadius: 6,
                    barPercentage: 0.6,
                    categoryPercentage: 0.7
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        font: { family: "'Inter', sans-serif", size: 12, weight: '500' }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.dataset.label + ': ₹' + Number(context.raw).toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#F1F5F9' },
                    ticks: {
                        callback: function (val) { return '₹' + val.toLocaleString('en-IN'); }
                    }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

/**
 * 2. Doughnut Chart: Expense Category Distribution
 */
function renderCategoryDoughnutChart(canvasId, labels, dataValues) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const palette = [
        '#6366F1', '#EC4899', '#F59E0B', '#10B981', '#3B82F6',
        '#8B5CF6', '#14B8A6', '#F97316', '#64748B', '#06B6D4'
    ];

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataValues,
                backgroundColor: palette.slice(0, labels.length),
                borderWidth: 2,
                borderColor: '#FFFFFF',
                hoverOffset: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        font: { family: "'Inter', sans-serif", size: 11, weight: '500' },
                        padding: 14
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return ' ' + context.label + ': ₹' + Number(context.raw).toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            }
        }
    });
}

/**
 * 3. Line Chart: Monthly Spending Trend
 */
function renderSpendingTrendLineChart(canvasId, labels, spendingData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Monthly Spending (₹)',
                data: spendingData,
                borderColor: '#6366F1',
                backgroundColor: 'rgba(99, 102, 241, 0.08)',
                fill: true,
                tension: 0.35,
                borderWidth: 3,
                pointBackgroundColor: '#6366F1',
                pointHoverRadius: 7,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return ' Spending: ₹' + Number(context.raw).toLocaleString('en-IN', { minimumFractionDigits: 2 });
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: '#F1F5F9' },
                    ticks: {
                        callback: function (val) { return '₹' + val.toLocaleString('en-IN'); }
                    }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}
