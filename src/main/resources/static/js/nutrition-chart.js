// Nutrition Chart Initialization
document.addEventListener('DOMContentLoaded', function() {
    try {
        // Get the canvas element
        const canvas = document.getElementById('nutritionChart');
        if (!canvas) {
            console.error('Canvas element not found');
            return;
        }
        
        // Get the 2D context
        const ctx = canvas.getContext('2d');
        if (!ctx) {
            console.error('Could not get 2D context from canvas');
            return;
        }
        
        // Get nutrition data from data attributes
        const protein = parseFloat(canvas.getAttribute('data-protein') || 0);
        const fat = parseFloat(canvas.getAttribute('data-fat') || 0);
        const carbs = parseFloat(canvas.getAttribute('data-carbs') || 0);
        
        console.log('Nutrition data:', { protein, fat, carbs });
        
        // Calculate calories
        const proteinCalories = protein * 4;
        const fatCalories = fat * 9;
        const carbsCalories = carbs * 4;
        
        console.log('Calorie breakdown:', { proteinCalories, fatCalories, carbsCalories });
        
        // Create the chart
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['蛋白质', '脂肪', '碳水化合物'],
                datasets: [{
                    data: [proteinCalories, fatCalories, carbsCalories],
                    backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc']
                }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const dataset = context.dataset;
                                const total = dataset.data.reduce((previousValue, currentValue) => previousValue + currentValue, 0);
                                const currentValue = dataset.data[context.dataIndex];
                                const percentage = Math.floor(((currentValue/total) * 100)+0.5);
                                return context.chart.data.labels[context.dataIndex] + ': ' + percentage + '%';
                            }
                        }
                    },
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error creating chart:', error);
    }
});