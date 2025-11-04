import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-amenities-selector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="amenities-box p-3 rounded-3 bg-light">
    <div class="d-flex flex-wrap gap-3">
      <div *ngFor="let amenity of amenities" class="form-check form-check-inline">
        <input
          class="form-check-input"
          type="checkbox"
          [checked]="selectedAmenities.includes(amenity.id)"
          (change)="toggleAmenity(amenity.id)">
        <label class="form-check-label">{{ amenity.name }}</label>
      </div>
    </div>

    <div class="mt-3">
      <label class="form-label">Add Custom Amenity</label>
      <div class="input-group">
        <input type="text" [(ngModel)]="customAmenity" class="form-control" placeholder="e.g. Rooftop Garden">
        <button class="btn btn-outline-primary" type="button" (click)="addCustomAmenity()">Add</button>
      </div>
    </div>
  </div>
  `,
  styles: [`
    .amenities-box {
      border: 1px solid #ddd;
      background: #fff;
    }
  `]
})
export class AmenitiesSelector {
  @Input() selectedAmenities: number[] = [];
  @Output() selectedAmenitiesChange = new EventEmitter<number[]>();

  amenities: any[] = [];
  customAmenity = '';
  loading = true;

  constructor(private http: HttpClient, private toastr: ToastrService) {}

  ngOnInit() { this.loadAmenities(); }

  loadAmenities() {
    this.http.get<any[]>(`${environment.apiUrl}/api/amenities`).subscribe({
      next: data => { this.amenities = data; this.loading = false; },
      error: () => { this.toastr.error('Failed to load amenities'); this.loading = false; }
    });
  }

  toggleAmenity(id: number) {
    const index = this.selectedAmenities.indexOf(id);
    if (index > -1) this.selectedAmenities.splice(index, 1);
    else this.selectedAmenities.push(id);
    this.selectedAmenitiesChange.emit(this.selectedAmenities);
  }

  addCustomAmenity() {
    const name = this.customAmenity.trim();
    if (!name) return;

this.http.post<any>(`${environment.apiUrl}/api/amenities`, { name }).subscribe({
  next: (newAmenity) => {
    this.amenities.push(newAmenity);
    this.selectedAmenities.push(newAmenity.id);
    this.selectedAmenitiesChange.emit(this.selectedAmenities);
    this.customAmenity = '';
    this.toastr.success('Amenity added successfully');
  },
  error: () => this.toastr.error('Failed to add amenity')
});
  }
}
