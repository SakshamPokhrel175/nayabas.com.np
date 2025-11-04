import { TestBed } from '@angular/core/testing';

import { Amenity } from './amenity';

describe('Amenity', () => {
  let service: Amenity;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Amenity);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
