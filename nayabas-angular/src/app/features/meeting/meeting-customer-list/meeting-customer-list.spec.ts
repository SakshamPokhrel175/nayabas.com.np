import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetingCustomerList } from './meeting-customer-list';

describe('MeetingList', () => {
  let component: MeetingCustomerList;
  let fixture: ComponentFixture<MeetingCustomerList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeetingCustomerList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeetingCustomerList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
