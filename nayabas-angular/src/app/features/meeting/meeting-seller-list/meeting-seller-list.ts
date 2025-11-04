import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Meeting, MeetingProposeChange, MeetingService } from '../meeting';
import { ToastrService } from 'ngx-toastr';
import { WebsocketService } from '../../../core/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-meeting-seller-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './meeting-seller-list.html',
  styleUrls: ['./meeting-seller-list.scss'],
})
export class MeetingSellerList implements OnInit, OnDestroy {
  meetings: Meeting[] = [];
  loading = true;
  actionInProgress = false;

    private reminderInterval: any; // 🏆 For persistent reminders
  private readonly REMINDER_INTERVAL_MS = 300000; // 5 minutes

  constructor(
    private meetingService: MeetingService,
    private toastr: ToastrService,
    private webSocketService: WebsocketService // ✅ Inject WebSocket
  ) {}

 ngOnInit(): void {
    this.loadMeetings();
    this.webSocketService.connect();

    // 1. Real-time update subscription
    this.webSocketService.meetingUpdates$.subscribe((updatedMeeting) => {
      const index = this.meetings.findIndex((m) => m.id === updatedMeeting.id);
      const isNewRequest = (index < 0);
      const status = updatedMeeting.meetingStatus;
      const customerName = updatedMeeting.customer?.fullName || updatedMeeting.customer?.username || 'A customer';

      // Update local array and trigger UI flash
      if (index >= 0) {
        this.meetings[index] = { ...updatedMeeting };
      } else {
        this.meetings = [updatedMeeting, ...this.meetings];
      }
      this.flashRow(updatedMeeting.id);

      // 🔔 Immediate Notifications for Seller
      if (isNewRequest && status === 'PENDING') {
          // 👑 Show persistent alert immediately when new request is received
          this.showActionRequiredAlerts(); 
      } else if (status === 'SCHEDULED') {
          this.toastr.success(`Meeting with ${customerName} CONFIRMED.`, 'SCHEDULED');
      } else if (status === 'REJECTED') {
          this.toastr.warning(`Meeting with ${customerName} was rejected.`, 'REJECTED');
      }
    });
    
    // 2. 🏆 Persistent Reminders
    this.startPersistentReminders();
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
    if (this.reminderInterval) {
        clearInterval(this.reminderInterval); // 🏆 Cleanup the timer
    }
  }
    
  loadMeetings() {
    this.loading = true;
    this.meetingService.getSellerMeetings().subscribe({
      next: (data) => {
        this.meetings = data || [];
        this.loading = false;
        this.showActionRequiredAlerts(); // Show alert immediately after loading data
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Failed to load meetings', 'Error');
        this.loading = false;
      },
    });
  }
    
  // 🏆 NEW: Persistent Reminder Logic
  startPersistentReminders() {
    // Set interval to check for unaddressed meetings
    this.reminderInterval = setInterval(() => {
      this.showActionRequiredAlerts();
    }, this.REMINDER_INTERVAL_MS);
  }
    
  showActionRequiredAlerts() {
    const actionRequiredMeetings = this.meetings.filter(m => m.meetingStatus === 'PENDING');

    if (actionRequiredMeetings.length > 0) {
      const count = actionRequiredMeetings.length;
      const listHtml = actionRequiredMeetings.map(m => 
          `<li><strong>${m.property?.title || 'Property'}</strong> requested by <strong>${m.customer?.fullName || 'Customer'}</strong>.</li>`
      ).join('');
      
      Swal.fire({
        title: `🚨 ${count} PENDING MEETING LEAD${count > 1 ? 'S' : ''}!`,
        html: `
            <p>You have ${count} meeting request${count > 1 ? 's' : ''} requiring immediate action:</p>
            <ul class="text-start">${listHtml}</ul>
            <p class="text-danger small mt-3">Please Accept, Reject, or Propose a change now.</p>
        `,
        icon: 'warning',
        confirmButtonText: 'View Requests',
        timer: 15000, 
        timerProgressBar: true,
        position: 'top-end',
        toast: true, 
        background: '#fff3cd', // Warning background for prominence
        customClass: {
          title: 'text-dark fw-bold',
          popup: 'border border-warning'
        }
      });
    }
  }

  // ... (rest of accept, reject, openProposeModal, sendProposal, flashRow methods remain unchanged)

  accept(meeting: Meeting) {
    if (this.actionInProgress) return;
    this.actionInProgress = true;
    this.meetingService.updateStatus(meeting.id, 'SCHEDULED').subscribe({
      next: () => {
        this.toastr.success('Meeting accepted', 'Success');
        this.actionInProgress = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Failed to accept meeting', 'Error');
        this.actionInProgress = false;
      },
    });
  }

  reject(meeting: Meeting) {
    if (this.actionInProgress) return;
    this.actionInProgress = true;
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
  }

  // 💫 Smooth flash animation for updates
  flashRow(meetingId: number) {
    const row = document.getElementById(`meeting-${meetingId}`);
    if (row) {
      row.classList.add('flash');
      setTimeout(() => row.classList.remove('flash'), 1000);
    }
  }

   // 💡 NEW METHOD: Seller opens modal to propose a change
  openProposeModal(meeting: Meeting) {
    if (this.actionInProgress) return;
    this.actionInProgress = true;
    
    // Use SweetAlert2 with form fields for professional input
    Swal.fire({
      title: 'Propose New Meeting Time',
      html: `
        <input id="swal-date" type="date" class="swal2-input" value="${meeting.meetingDate || ''}" placeholder="New Date">
        <input id="swal-time" type="time" class="swal2-input" value="${meeting.meetingTime || ''}" placeholder="New Time">
        <textarea id="swal-note" class="swal2-textarea" placeholder="Note to Customer (Optional)"></textarea>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Send Proposal',
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const newDate = (document.getElementById('swal-date') as HTMLInputElement).value;
        const newTime = (document.getElementById('swal-time') as HTMLInputElement).value;
        const sellerNote = (document.getElementById('swal-note') as HTMLTextAreaElement).value;

        if (!newDate || !newTime) {
          Swal.showValidationMessage('Please select both date and time');
          return false;
        }
        return { newDate, newTime, sellerNote };
      }
    }).then((result) => {
      this.actionInProgress = false; // Reset action status regardless of outcome
      
      if (result.isConfirmed) {
        const payload: MeetingProposeChange = {
          newDate: result.value.newDate,
          newTime: result.value.newTime,
          sellerNote: result.value.sellerNote || undefined
        };
        this.sendProposal(meeting.id, payload);
      }
    });
  }

  // 💡 NEW METHOD: Calls API to send the change proposal
  sendProposal(meetingId: number, req: MeetingProposeChange) {
    this.actionInProgress = true;
    this.meetingService.proposeChange(meetingId, req).subscribe({
      next: () => {
        this.toastr.info('New time proposed! Waiting for customer confirmation.', 'Proposal Sent');
      },
      error: (err) => {
        console.error(err);
        this.toastr.error(err.error?.message || 'Failed to propose change.', 'Error');
      },
      complete: () => {
        this.actionInProgress = false;
      }
    });
  }
}
