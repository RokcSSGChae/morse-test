package com.morse.streaming.model;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.kurento.client.IceCandidate;
import org.kurento.client.WebRtcEndpoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
@NoArgsConstructor
public class JoinedViewer {
	private int viewerIdx;
	private String nickname;
	private String webRtcEndpoint;

	@Builder
	public JoinedViewer(int viewerIdx, String nickname, String webRtcEndpoint) {
		this.viewerIdx = viewerIdx;
		this.nickname = nickname;
		this.webRtcEndpoint = webRtcEndpoint;
	}
}
