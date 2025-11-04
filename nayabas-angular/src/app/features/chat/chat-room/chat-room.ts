import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router'; // 🛑 Inject Router for redirection
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { ChatApi } from '../chat-api';

@Component({
  selector: 'app-chat-room',
  standalone: true,
  imports: [CommonModule], // Ensure CommonModule is here if using *ngIf etc.
  templateUrl: './chat-room.html',
  styleUrl: './chat-room.scss'
})
export class ChatRoom implements OnInit, OnDestroy {
    
    roomId: string = '';
    // isChatActive: boolean = true; // Use this later for disabling chat inputs
    
    // 🛑 Inject necessary services
    constructor(
        private route: ActivatedRoute, 
        private router: Router, 
        private chatApi: ChatApi,
        private toastr: ToastrService
    ) {}
    ngOnInit(): void {
        // Get the roomId from the URL route parameter
        this.roomId = this.route.snapshot.paramMap.get('roomId') || '';
        
        if (!this.roomId || this.roomId === 'null') { // 🛑 Explicitly check for 'null' string
            this.router.navigate(['/customer/meeting']);
            this.toastr.error('Chat room ID is invalid or missing.', 'Error');
            return;
        }
        
        // 💡 The chat room logic proceeds only if roomId is valid
        console.log(`[ChatRoom] Connecting to WebSocket room: /topic/chat/${this.roomId}`);
        // ...
    }

    ngOnDestroy(): void {
        // 💡 TODO: Disconnect WebSocket when leaving the component
    }

    /**
     * Triggers the API call to backend to destroy the room and finalize the meeting status.
     */
    endChatSession(): void {
        Swal.fire({
            title: 'End Session?',
            text: "Ending the chat will deactivate the link and mark the meeting as complete. Proceed to booking?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, End Chat',
            cancelButtonText: 'Cancel',
            confirmButtonColor: '#dc3545',
        }).then((result) => {
            if (result.isConfirmed) {
                this.chatApi.endChatSession(this.roomId).subscribe({
                    next: (res) => {
                        this.toastr.success(res.message || 'Chat ended. Proceed to booking.', 'Success');
                        
                        // 🛑 REDIRECT: Go back to the meetings list
                        this.router.navigate(['/customer/meeting']);
                        
                        // NOTE: WebSocket notification from the backend will update the table automatically
                    },
                    error: (err) => {
                        console.error('Failed to end chat:', err);
                        this.toastr.error('Failed to end chat session.', 'Error');
                    }
                });
            }
        });
    }
    
    // 💡 Placeholder for message sending logic
    sendMessage(message: string) { 
        if (!message) return;
        console.log(`[Chat] Sending message to ${this.roomId}: ${message}`);
        // 💡 TODO: Implement WebSocket send logic here
    }
}