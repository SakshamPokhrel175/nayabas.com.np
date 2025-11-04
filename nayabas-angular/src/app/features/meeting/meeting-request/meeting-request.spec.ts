import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetingRequest } from './meeting-request';

describe('MeetingRequest', () => {
  let component: MeetingRequest;
  let fixture: ComponentFixture<MeetingRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeetingRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeetingRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
