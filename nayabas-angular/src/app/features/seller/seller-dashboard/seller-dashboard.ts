import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Chart, ChartData, ChartOptions, registerables } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { PropertyService, Property } from '../../property/property';

Chart.register(...registerables);

interface EarningsResponse {
  [month: string]: number; // e.g., { Jan: 1200, Feb: 800 }
}

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, BaseChartDirective],
  templateUrl: './seller-dashboard.html',
  styleUrls: ['./seller-dashboard.scss']
})
export class SellerDashboard implements OnInit {
  @ViewChild(BaseChartDirective) earningsChart?: BaseChartDirective;

  properties: Property[] = [];
  loading = true;
  error = '';

  earningsChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Monthly Earnings (Rs.)',
        fill: true,
        borderColor: '#4e73df',
        backgroundColor: 'rgba(78, 115, 223, 0.1)',
        tension: 0.3,
        pointBackgroundColor: '#4e73df'
      }
    ]
  };

  earningsChartOptions: ChartOptions<'line'> = {
    responsive: true,
    plugins: {
      legend: { display: true, position: 'top' },
      tooltip: { mode: 'index', intersect: false }
    },
    scales: {
      x: { title: { display: true, text: 'Month' } },
      y: { title: { display: true, text: 'Earnings (Rs.)' } }
    },
    animation: { duration: 800, easing: 'easeOutQuart' }
  };

  constructor(
    private propertyService: PropertyService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadProperties();
      this.loadMonthlyEarnings(); // NEW
  }

  // Load all properties for seller
  loadProperties(): void {
    this.loading = true;
    this.propertyService.getMyProperties().subscribe({
      next: (data: Property[]) => {
        this.properties = data;
        this.loading = false;
        this.updateChart();
      },
      error: () => {
        this.error = 'Failed to load properties';
        this.loading = false;
        this.toastr.error(this.error);
      }
    });
  }

  deleteProperty(id: number): void {
    if (!confirm('Are you sure you want to delete this property?')) return;

    this.propertyService.delete(id).subscribe({
      next: () => {
        this.properties = this.properties.filter(p => p.id !== id);
        this.updateChart();
        this.toastr.success('Property deleted successfully');
      },
      error: () => this.toastr.error('Failed to delete property')
    });
  }

  updateChart(): void {
    if (!this.properties.length) return;

    const monthlyEarnings: { [month: string]: number } = {};

    this.properties.forEach(p => {
      // Use createdAt if available, otherwise use current date
      const createdAt = (p as any).createdAt ? new Date((p as any).createdAt) : new Date();
      const month = createdAt.toLocaleString('default', { month: 'short' });
      monthlyEarnings[month] = (monthlyEarnings[month] || 0) + (p.price || 0);
    });

    const allMonths = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    const labels = allMonths.filter(m => monthlyEarnings[m] !== undefined);
    const values: number[] = labels.map(m => monthlyEarnings[m]);

    // Create gradient fill
    let gradient: CanvasGradient | string = 'rgba(78, 115, 223, 0.1)';
    const chart = this.earningsChart?.chart;
    if (chart?.ctx) {
      const ctx = chart.ctx;
      const g = ctx.createLinearGradient(0, 0, 0, 400);
      g.addColorStop(0, 'rgba(78, 115, 223, 0.4)');
      g.addColorStop(1, 'rgba(78, 115, 223, 0.05)');
      gradient = g;
    }

    this.earningsChartData.labels = labels;
    this.earningsChartData.datasets[0] = {
      ...this.earningsChartData.datasets[0],
      data: values,
      backgroundColor: gradient,
      borderColor: '#4e73df',
      tension: 0.3,
      fill: true,
      pointBackgroundColor: '#4e73df'
    };
  }

  get totalEarnings(): number {
    return this.properties.reduce((sum, p) => sum + (p.price || 0), 0);
  }

  get totalProperties(): number {
    return this.properties.length;
  }

  getPropertyImage(property: Property): string {
  const img = property.images?.[0]?.imageData;
  return img ? 'data:image/jpeg;base64,' + img : 'assets/default-property.jpg';
}



  // NEW METHOD
loadMonthlyEarnings(): void {
  this.propertyService.getMonthlyEarnings().subscribe({
    next: (earnings) => {
      const labels = Object.keys(earnings);
      const values = Object.values(earnings).map(v => Number(v));

      // Create gradient
      let gradient: CanvasGradient | string = 'rgba(78, 115, 223, 0.1)';
      const chart = this.earningsChart?.chart;
      if (chart?.ctx) {
        const ctx = chart.ctx;
        const g = ctx.createLinearGradient(0, 0, 0, 400);
        g.addColorStop(0, 'rgba(78, 115, 223, 0.4)');
        g.addColorStop(1, 'rgba(78, 115, 223, 0.05)');
        gradient = g;
      }

      this.earningsChartData.labels = labels;
      this.earningsChartData.datasets[0] = {
        ...this.earningsChartData.datasets[0],
        data: values,
        backgroundColor: gradient,
        borderColor: '#4e73df',
        tension: 0.3,
        fill: true,
        pointBackgroundColor: '#4e73df'
      };
    },
    error: () => {
      this.toastr.error('Failed to load earnings data');
    }
  });
}
}