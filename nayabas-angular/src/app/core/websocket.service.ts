import { Injectable } from '@angular/core';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Swal from 'sweetalert2';
import { Howl } from 'howler';
import { Subject } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private stompClient?: Client;
  private meetingUpdates = new Subject<any>();
  meetingUpdates$ = this.meetingUpdates.asObservable();

  private notificationSound = new Howl({
    src: ['https://actions.google.com/sounds/v1/alarms/beep_short.ogg'],
    volume: 0.5,
  });

  connect(): void {
    const token = localStorage.getItem('token');
    const socket = new SockJS(`${environment.apiUrl}/ws?token=${token}`);
    this.stompClient = Stomp.over(socket);

    this.stompClient.onConnect = () => {
      console.log('✅ Connected to WebSocket');
      this.stompClient?.subscribe('/topic/meetings', (message: IMessage) => {
        if (message.body) {
          const meeting = JSON.parse(message.body);
          this.showNotification(meeting);
          this.meetingUpdates.next(meeting);
        }
      });
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient?.active) {
      this.stompClient.deactivate().then(() => console.log('WebSocket disconnected'));
    }
  }

  private showNotification(meeting: any): void {
    this.notificationSound.play();

    const title =
      meeting.meetingStatus === 'PENDING'
        ? '📩 New Meeting Request'
        : `✅ Meeting ${meeting.meetingStatus}`;
    const text =
      meeting.meetingStatus === 'PENDING'
        ? `New meeting requested by ${meeting.customerName || 'a buyer'}.`
        : `Meeting with ${meeting.customerName || 'a buyer'} is now ${meeting.meetingStatus}.`;

    Swal.fire({
      title,
      text,
      icon: 'info',
      toast: true,
      position: 'top-end',
      timer: 4000,
      showConfirmButton: false,
    });
  }
}
