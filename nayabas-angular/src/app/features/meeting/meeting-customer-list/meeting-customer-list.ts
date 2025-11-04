import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Meeting, MeetingService } from '../meeting';
import { WebsocketService } from '../../../core/websocket.service';
import Swal from 'sweetalert2';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-meeting-customer-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './meeting-customer-list.html',
  styleUrls: ['./meeting-customer-list.scss'],
})
export class MeetingCustomerList implements OnInit, OnDestroy {
  meetings: Meeting[] = [];
  loading = true;
  actionInProgress = false;
  chatRoomId?: string | undefined;

  private reminderInterval: any; // 🏆 For persistent reminders
  private readonly REMINDER_INTERVAL_MS = 300000; // 5 minutes

  constructor(
    private meetingService: MeetingService,
    private toastr: ToastrService,
    private webSocketService: WebsocketService, // ✅ Inject WebSocket
    private router: Router // 🛑 INJECT ROUTER
  ) {}

  ngOnInit(): void {
    this.loadMeetings();
    this.webSocketService.connect(); // 1. Real-time update subscription

    this.webSocketService.meetingUpdates$.subscribe((updatedMeeting) => {
      const index = this.meetings.findIndex((m) => m.id === updatedMeeting.id);
      const status = updatedMeeting.meetingStatus;
      const meetingTitle = updatedMeeting.property?.title || 'a property';
      const wasInList = index >= 0;
      if (wasInList) {
        this.meetings[index] = { ...updatedMeeting };
      } else {
        this.meetings = [updatedMeeting, ...this.meetings];
      }
      this.flashRow(updatedMeeting.id); // 🔔 Immediate Notifications for Customer

      if (status === 'PROPOSED_CHANGE') {
        this.toastr.info(
          `Seller proposed a new time for ${meetingTitle}. Please review!`,
          'ACTION REQUIRED'
        );
      } else if (status === 'SCHEDULED' && wasInList) {
        this.toastr.success(
          `Your meeting for ${meetingTitle} is now SCHEDULED!`,
          'MEETING CONFIRMED'
        );
      } else if (status === 'REJECTED') {
        this.toastr.error(
          `Your meeting request for ${meetingTitle} was rejected by the seller.`,
          'MEETING REJECTED'
        );
      }
    }); // 2. 🏆 Persistent Reminders
    this.startPersistentReminders();
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
    if (this.reminderInterval) {
      clearInterval(this.reminderInterval);
    }
  }

  loadMeetings() {
    this.loading = true;
    this.meetingService.getCustomerMeetings().subscribe({
      next: (data) => {
        this.meetings = data || [];
        this.loading = false;
        this.showActionRequiredAlerts();
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Failed to load meetings', 'Error');
        this.loading = false;
      },
    });
  } // 🏆 NEW: Persistent Reminder Logic

  startPersistentReminders() {
    // Set interval to check for unaddressed meetings
    this.reminderInterval = setInterval(() => {
      this.showActionRequiredAlerts();
    }, this.REMINDER_INTERVAL_MS);
  }

  showActionRequiredAlerts() {
    const actionRequiredMeetings = this.meetings.filter(
      (m) => m.meetingStatus === 'PROPOSED_CHANGE'
    );

    if (actionRequiredMeetings.length > 0) {
      const count = actionRequiredMeetings.length;
      const listHtml = actionRequiredMeetings
        .map(
          (m) =>
            `<li>Review new time for <strong>${m.property?.title || 'Property'}</strong>: <strong>${
              m.meetingDate
            }</strong> at <strong>${m.meetingTime}</strong>.</li>`
        )
        .join('');
      Swal.fire({
        title: `🔔 ${count} PENDING PROPOSAL${count > 1 ? 'S' : ''}!`,
        html: `
            <p>The Seller has proposed ${count} new time${
          count > 1 ? 's' : ''
        } you need to confirm:</p>
            <ul class="text-start">${listHtml}</ul>
            <p class="text-info small mt-3">Click 'Confirm/Reject' to finalize the meeting.</p>
        `,
        icon: 'info',
        confirmButtonText: 'Review Now',
        timer: 15000,
        timerProgressBar: true,
        position: 'top-end',
        toast: true,
        background: '#ebf4ff', // Light blue background for info
        customClass: {
          title: 'text-primary fw-bold',
          popup: 'border border-primary',
        },
      });
    }
  } // ... (rest of accept, confirmChange, reject, flashRow methods remain unchanged) // 🛑 LEGACY: This method should be removed as customers cannot schedule meetings directly. // The SCHEDULED status is only set by Seller Accept or Customer ConfirmChange.

  // accept(meeting: Meeting) {
  //   if (this.actionInProgress) return;
  //   this.actionInProgress = true;
  //   this.meetingService.updateStatus(meeting.id, 'SCHEDULED').subscribe({
  //     next: () => {
  //       this.toastr.success('Meeting accepted', 'Success');
  //       this.actionInProgress = false;
  //     },
  //     error: (err) => {
  //       console.error(err);
  //       this.toastr.error('Failed to accept meeting', 'Error');
  //       this.actionInProgress = false;
  //     },
  //   });
  // }

  accept(meeting: Meeting) {
    this.toastr.warning('Only the seller can accept the initial meeting request.', 'Action Denied');
  }

  // 💡 NEW METHOD: Ask customer if they want to book
  askToBook(meeting: Meeting) {
    Swal.fire({
      title: 'Ready to Book?',
      text: `The meeting for "${meeting.property?.title}" is complete. Do you wish to proceed with the booking?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, Book Now!',
      cancelButtonText: 'Maybe Later',
      confirmButtonColor: '#4e54c8',
    }).then((result) => {
      if (result.isConfirmed) {
        // 🛑 Navigates to the booking form/page
         this.router.navigate(['/booking/create', meeting.property?.id]); 
      }
    });
  } // 💡 MODIFY confirmChange to trigger the popup after scheduling

  // // 💡 NEW METHOD: Customer confirms seller's proposed change
  // confirmChange(meeting: Meeting) {
  //   if (this.actionInProgress) return;
  //   this.actionInProgress = true;

  //   Swal.fire({
  //       title: 'Confirm New Time?',
  //       html: `The Seller proposed a new meeting time:<br><strong>${meeting.meetingDate} at ${meeting.meetingTime}</strong>.<br>Do you accept this change?`,
  //       icon: 'question',
  //       showCancelButton: true,
  //       confirmButtonText: 'Yes, Confirm',
  //       cancelButtonText: 'No, Reject',
  //       confirmButtonColor: '#28a745',
  //       cancelButtonColor: '#dc3545',
  //   }).then((result) => {
  //       if (result.isConfirmed) {
  //           this.meetingService.confirmChange(meeting.id).subscribe({ // 💡 NEW SERVICE CALL
  //               next: () => {
  //                   this.toastr.success('Meeting confirmed!', 'Scheduled');
  //               },
  //               error: (err) => {
  //                   this.toastr.error(err.error?.message || 'Failed to confirm change.', 'Error');
  //               },
  //               complete: () => {
  //                   this.actionInProgress = false;
  //               }
  //           });
  //       } else if (result.dismiss === Swal.DismissReason.cancel) {
  //           // If they cancel/reject the proposal, we call the regular reject method
  //           this.reject(meeting, true); // true indicates a rejection of a proposed change
  //       } else {
  //           this.actionInProgress = false;
  //       }
  //   });
  // }

  confirmChange(meeting: Meeting) {
    if (this.actionInProgress) return;
    this.actionInProgress = true;
    Swal.fire({
      // ... (existing Swal.fire for confirmation) ...
    }).then((result) => {
      if (result.isConfirmed) {
        this.meetingService.confirmChange(meeting.id).subscribe({
          next: (updatedMeeting) => {
            this.toastr.success('Meeting confirmed!', 'Scheduled'); // 🛑 TRIGGER THE FOLLOW-UP POPUP
            this.askToBook(updatedMeeting);
          },
          error: (err) => {
            this.toastr.error(err.error?.message || 'Failed to confirm change.', 'Error');
          },
          complete: () => {
            this.actionInProgress = false;
          },
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        this.reject(meeting, true);
      } else {
        this.actionInProgress = false;
      }
    });
  }

  // REVISED METHOD: Handle rejection of PENDING or PROPOSED_CHANGE
  reject(meeting: Meeting, isProposedRejection: boolean = false) {
    if (this.actionInProgress) return;
    this.actionInProgress = true;

    const title = isProposedRejection ? 'Reject Proposed Change?' : 'Reject Meeting?';
    const text = isProposedRejection
      ? 'Do you want to revert this meeting to REJECTED status?'
      : 'Are you sure you want to reject this meeting?';

    Swal.fire({
      title: title,
      text: text,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, Reject',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#dc3545',
    }).then((result) => {
      if (result.isConfirmed) {
        this.meetingService.updateStatus(meeting.id, 'REJECTED').subscribe({
          next: () => {
            this.toastr.success('Meeting rejected', 'Success');
            this.actionInProgress = false;
          },
          error: (err) => {
            console.error(err);
            this.toastr.error('Failed to reject meeting', 'Error');
            this.actionInProgress = false;
          },
        });
      } else {
        this.actionInProgress = false;
      }
    });
  }

  // 💫 Smooth flash animation for updates
  flashRow(meetingId: number) {
    const row = document.getElementById(`meeting-${meetingId}`);
    if (row) {
      row.classList.add('flash');
      setTimeout(() => row.classList.remove('flash'), 1000);
    }
  }
}
