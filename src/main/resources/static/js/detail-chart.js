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

        // Get nutrition data from hidden inputs
        const protein = parseFloat(document.getElementById('protein-value').value || 0);
        const fat = parseFloat(document.getElementById('fat-value').value || 0);
        const carbs = parseFloat(document.getElementById('carbs-value').value || 0);

        console.log('Nutrition data:', { protein, fat, carbs });

        // Calculate calories
        const proteinCalories = protein * 4;
        const fatCalories = fat * 9;
        const carbsCalories = carbs * 4;

        console.log('Calorie breakdown:', { proteinCalories, fatCalories, carbsCalories });

        // Create the chart
        new Chart(ctx, {
            type: 'pie',  // Changed from doughnut to pie to match the original
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

// Button event handlers
document.addEventListener('DOMContentLoaded', function() {
    // Convert to log button
    const convertToLogBtn = document.getElementById('convertToLogBtn');
    if (convertToLogBtn) {
        convertToLogBtn.addEventListener('click', function() {
            if (confirm('确定要将此计划转为历史记录吗？')) {
                const planId = document.getElementById('plan-id').value;
                fetch('/dietPlan/convertToLog/' + planId)
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            window.location.href = '/dietPlan/detail/' + data;
                        } else {
                            alert('转换失败，请稍后重试');
                        }
                    });
            }
        });
    }

    // Delete plan button
    const deletePlanBtn = document.getElementById('deletePlanBtn');
    if (deletePlanBtn) {
        deletePlanBtn.addEventListener('click', function() {
            const planId = document.getElementById('plan-id').value;
            const recordType = document.getElementById('record-type').value;
            if (confirm('确定要删除此' + (recordType === '计划' ? '计划' : '记录') + '吗？此操作不可恢复！')) {
                fetch('/dietPlan/delete/' + planId)
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            window.location.href = '/dietPlan/plans';
                        } else {
                            alert('删除失败，请稍后重试');
                        }
                    });
            }
        });
    }
});
