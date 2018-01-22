package kr.co.cntt.api.security;

public class ActorSession {
	private ThreadLocal<Actor> actorLocal = new ThreadLocal<>();

	public ActorSession add(final Actor actor) {
		actorLocal.set(actor);
		return this;
	}

	public ActorSession remove() {
		actorLocal.remove();
		return this;
	}

	public Actor actor() {
		Actor actor = actorLocal.get();
		return actor != null ? actor : null;
	}
}
