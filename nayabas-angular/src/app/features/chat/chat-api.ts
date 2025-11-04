import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // 🛑 NEW IMPORT
import { Observable } from 'rxjs'; // 🛑 NEW IMPORT
import { environment } from '../../../environments/environment'; // 🛑 NEW IMPORT

@Injectable({
  providedIn: 'root'
})
export class ChatApi {
    
    private apiUrl = `${environment.apiUrl}/api/chat`; // Matches your backend ChatController mapping

    constructor(private http: HttpClient) {} // 🛑 Inject HttpClient

    /**
     * Calls the backend endpoint to mark the chat room as destroyed 
     * and update the meeting status to CHAT_COMPLETED.
     */
    endChatSession(roomId: string): Observable<any> {
        // Calls: POST /api/chat/{roomId}/end
        return this.http.post(`${this.apiUrl}/${roomId}/end`, {});
    }
}