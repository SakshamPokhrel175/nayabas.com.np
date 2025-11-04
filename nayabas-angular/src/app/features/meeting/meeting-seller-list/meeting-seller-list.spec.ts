import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetingSellerList } from './meeting-seller-list';

describe('MeetingSellerList', () => {
  let component: MeetingSellerList;
  let fixture: ComponentFixture<MeetingSellerList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeetingSellerList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeetingSellerList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
