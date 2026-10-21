/**
 * Lahenduse loomisel kasutasin tehisintellekti abi (Claude Sonnet 5.5).
 */
public class LongStack implements Cloneable {
   // used resource: https://www.geeksforgeeks.org/dsa/implement-a-stack-using-singly-linked-list/

   private static class Node {
      private long stack;
      private Node next;

      private Node(long stack, Node next) {
         this.stack = stack;
         this.next = next;
      }
   }
   private Node top;

   public static void main (String[] argum) {
      System.out.println(interpret("15 7 3 >R - R> -"));
      System.out.println(interpret("3 >R R@ R> *"));
      System.out.println(interpret("2 5 >R 3 R@ * - R> -"));
   }

   // Constructor for a new stack

   LongStack() {
      this.top = null;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack clonedStack = (LongStack) super.clone();
      if (this.top == null) {
         clonedStack.top = null;
      } else {
         clonedStack.top = new Node(this.top.stack, null);
         Node currentOriginal = this.top.next;
         Node currentClone = clonedStack.top;
         while (currentOriginal != null) {
            currentClone.next = new Node(currentOriginal.stack, null);
            currentClone = currentClone.next;
            currentOriginal = currentOriginal.next;
         }
      }
      return clonedStack;
   }

   public boolean stEmpty() {
      return this.top == null;
   }

   public void push (long a) {
      this.top = new Node(a, this.top);
   }

   public long pop() {
      if (stEmpty()) {
         throw new RuntimeException("Stack underflow error: Can't pop in empty stack");
      }
      long value = this.top.stack;
      this.top = this.top.next;
      return value;
   }

   public void op (String s) {
      if (s == null || (!s.equals("+") && !s.equals("-") && !s.equals("*") && !s.equals("/"))) {
         throw new RuntimeException("Invalid operation error: '" + s + "' is not a supported mathematical operator (+, -, *, /).");
      }

      if (this.top == null || this.top.next == null) {
         throw new RuntimeException("Stack underflow error: Operation '" + s + "' requires at least 2 elements.");
      }

      long y = pop();
      long x = pop();
      long result = 0;

      switch (s) {
         case "+": result = x + y; break;
         case "-": result = x - y; break;
         case "*": result = x * y; break;
         case "/":
            if (y == 0) {
               throw new RuntimeException("Arithmetic error: Can't divide by 0");
            }
            result = x / y;
            break;
      }
      push(result);
   }

   public long tos() {
      if (stEmpty()) {
         throw new RuntimeException("TOS error: Stack is empty.");
      }
      return this.top.stack;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      LongStack other = (LongStack) o;
      Node currentThis = this.top;
      Node currentOther = other.top;

      while (currentThis != null && currentOther != null) {
         if (currentThis.stack != currentOther.stack) {
            return false;
         }
         currentThis = currentThis.next;
         currentOther = currentOther.next;
      }

      return currentThis == null && currentOther == null;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      buildBottomToTopString(this.top, sb);
      return sb.toString().trim();
   }

   private void buildBottomToTopString(Node node, StringBuilder sb) {
      if (node == null) {
         return;
      }
      buildBottomToTopString(node.next, sb);
      sb.append(node.stack).append(" ");
   }

   public static long interpret (String pol) {

      if (pol == null || pol.trim().isEmpty()) {
         throw new RuntimeException("RPN Interpretation Error: Expression string is empty or null. Original input: \"" + pol + "\"");
      }
      LongStack stack = new LongStack();
      LongStack rStack = new LongStack(); // return stack (R-pinu)
      String[] tokens = pol.trim().split("\\s+");

      for (String token : tokens) {
         if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
            try {
               stack.op(token);
            } catch (RuntimeException e) {
               throw new RuntimeException("RPN Interpretation Error: Not enough numbers to perform the operation '" + token + "'. Original input: \"" + pol + "\"", e);
            }
         } else if (token.equals(">R")) {
            if (stack.stEmpty()) {
               throw new RuntimeException("RPN Interpretation Error: '>R' requires an element on the data stack, but it is empty. Original input: \"" + pol + "\"");
            }
            rStack.push(stack.pop());
         } else if (token.equals("R>")) {
            if (rStack.stEmpty()) {
               throw new RuntimeException("RPN Interpretation Error: 'R>' requires an element on the return stack, but it is empty. Original input: \"" + pol + "\"");
            }
            stack.push(rStack.pop());
         } else if (token.equals("R@")) {
            if (rStack.stEmpty()) {
               throw new RuntimeException("RPN Interpretation Error: 'R@' requires an element on the return stack, but it is empty. Original input: \"" + pol + "\"");
            }
            stack.push(rStack.tos());
         } else {
            try {
               long value = Long.parseLong(token);
               stack.push(value);
            } catch (NumberFormatException e) {
               throw new RuntimeException("RPN Interpretation Error: Illegal symbol '" + token + "' detected. Original input: \"" + pol + "\"", e);
            }
         }
      }

      if (!rStack.stEmpty()) {
         throw new RuntimeException("RPN Interpretation Error: Return stack is not empty at the end of the expression. Original input: \"" + pol + "\"");
      }

      long result = stack.pop();

      if (!stack.stEmpty()) {
         throw new RuntimeException("RPN Interpretation Error: Too many numbers provided. Redundant elements left on the stack. Original input: \"" + pol + "\"");
      }

      return result;
   }
}
