import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Chart, ChartData, ChartOptions, registerables } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { PropertyService, Property } from '../../property/property';
import { Admin, AdminStats, PendingSeller, User } from '../admin';
import { RouterModule } from '@angular/router';

declare var bootstrap: any; // Required for modal

Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, RouterModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.scss']
})
export class AdminDashboard implements OnInit {
  stats: AdminStats[] = [];
  pendingVerifications: PendingSeller[] = [];
  properties: Property[] = [];
  loading = true;
  error = '';

  @ViewChild(BaseChartDirective) earningsChart?: BaseChartDirective;

  earningsChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Monthly Earnings',
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

  // Modal
  modalUsers: User[] = [];
  modalTitle = '';

  constructor(
    private adminService: Admin,
    private propertyService: PropertyService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadPendingVerifications();
    this.loadProperties();
  }

  loadStats(): void {
    this.adminService.getStats().subscribe({
      next: (data) => this.stats = data,
      error: () => this.toastr.error('Failed to load statistics')
    });
  }

  loadPendingVerifications(): void {
    this.adminService.getPendingSellers().subscribe({
      next: (data) => this.pendingVerifications = data,
      error: () => this.toastr.error('Failed to load pending verifications')
    });
  }

  // loadProperties(): void {
  //   this.propertyService.getAll().subscribe({
  //     next: (data) => {
  //       this.properties = data;
  //       this.loading = false;
  //       this.updateChart();
  //     },
  //     error: () => {
  //       this.error = 'Failed to load properties';
  //       this.loading = false;
  //       this.toastr.error(this.error);
  //     }
  //   });
  // }

  loadProperties(): void {
  this.propertyService.getAllWithOwners().subscribe({
  next: (data) => {
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
  if (confirm('Are you sure you want to delete this property?')) {
    this.propertyService.delete(id).subscribe({
      next: () => {
        this.properties = this.properties.filter(p => p.id !== id);
        this.updateChart();
        this.toastr.success('Property deleted');
      },
      error: () => this.toastr.error('Delete failed')
    });
  }
}


  updateChart(): void {
    if (!this.properties.length) return;

    const monthlyEarnings: { [key: string]: number } = {};
    this.properties.forEach(p => {
      if (!p.createdAt || !p.price) return;
      const date = new Date(p.createdAt);
      const month = date.toLocaleString('default', { month: 'short' });
      monthlyEarnings[month] = (monthlyEarnings[month] || 0) + p.price;
    });

    const allMonths = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    const labels = allMonths.filter(m => monthlyEarnings[m] !== undefined);
    const earnings = labels.map(m => monthlyEarnings[m]);

    let ctx: CanvasRenderingContext2D | null = null;
    if (this.earningsChart && this.earningsChart.chart) ctx = this.earningsChart.chart.ctx;

    let gradient: CanvasGradient | undefined;
    if (ctx) {
      gradient = ctx.createLinearGradient(0, 0, 0, 400);
      gradient.addColorStop(0,'rgba(78, 115, 223, 0.4)');
      gradient.addColorStop(1,'rgba(78, 115, 223, 0.05)');
    }

    this.earningsChartData.labels = labels;
    this.earningsChartData.datasets[0] = {
      ...this.earningsChartData.datasets[0],
      data: earnings,
      backgroundColor: gradient || 'rgba(78, 115, 223, 0.1)',
      borderColor: '#4e73df',
      tension: 0.3,
      fill: true,
      pointBackgroundColor: '#4e73df'
    };

    this.earningsChart?.update();
  }

  approveSeller(userId: number): void {
  this.adminService.approveSeller(userId).subscribe({
    next: () => {
      this.pendingVerifications = this.pendingVerifications.filter(u => u.id !== userId);
      this.toastr.success('Seller approved');
    },
    error: () => this.toastr.error('Approval failed')
  });
}

rejectSeller(userId: number): void {
  this.adminService.rejectSeller(userId).subscribe({
    next: () => {
      this.pendingVerifications = this.pendingVerifications.filter(u => u.id !== userId);
      this.toastr.success('Seller rejected');
    },
    error: () => this.toastr.error('Rejection failed')
  });
}


  get totalEarnings(): number {
    return this.properties.reduce((sum,p) => sum + (p.price || 0), 0);
  }

  showUserList(key: 'totalSellers' | 'totalBuyers') {
  this.modalTitle = key === 'totalSellers' ? 'All Sellers' : 'All Buyers';
  const obs = key === 'totalSellers'
    ? this.adminService.getAllSellers()
    : this.adminService.getAllBuyers();

  obs.subscribe({
    next: (users) => {
      this.modalUsers = users; // users must have 'role'
      const modal = new bootstrap.Modal(document.getElementById('userListModal'));
      modal.show();
    },
    error: () => this.toastr.error('Failed to load users')
  });
}

getImageSrc(property: Property): string {
  const imageData = property.images?.[0]?.imageData;
  return imageData
    ? `data:image/jpeg;base64,${imageData}`
    : 'assets/default-property.jpg';
}

}
