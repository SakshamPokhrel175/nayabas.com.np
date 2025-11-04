import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AmenitiesSelector } from './amenities-selector';

describe('AmenitiesSelector', () => {
  let component: AmenitiesSelector;
  let fixture: ComponentFixture<AmenitiesSelector>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AmenitiesSelector]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AmenitiesSelector);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
