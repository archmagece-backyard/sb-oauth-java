package org.scriptonbasestar.oauth.client.model;

public class PairModel<A, B> {
  private A a;
  private B b;

  public PairModel(A a, B b) {
    this.a = a;
    this.b = b;
  }
}
