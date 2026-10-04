/**
 * Lahenduse loomisel kasutasin tehisintellekti abi (GPT-5.6 Luna).
 */
import java.util.LinkedList;

public class LongStack {

   private LinkedList<Long> stack;

   public static void main (String[] argum) {
      System.out.println(interpret("2 3 +"));
   }

   LongStack() {
      stack = new LinkedList<>();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LongStack copy = new LongStack();
      copy.stack.addAll(stack);
      return copy;
   }

   public boolean stEmpty() {
      return stack.isEmpty();
   }

   public void push (long a) {
      stack.add(a);
   }

   public long pop() {
      if (stEmpty()) {
         throw new RuntimeException("pop: stack is empty");
      }
      return stack.removeLast();
   } // pop

   public void op (String s) {
      if (!s.equals("+") && !s.equals("-") && !s.equals("*") && !s.equals("/")) {
         throw new RuntimeException("op: invalid operation '" + s + "'");
      }
      if (stack.size() < 2) {
         throw new RuntimeException("op: not enough elements for operation'" + s + "'");
      }
      long right = pop();
      long left = pop();

      if (s.equals("+")) {
         push(left + right);
      } else if (s.equals("-")) {
         push(left - right);
      } else if (s.equals("*")) {
         push(left * right);
      } else {
         if (right == 0) {
            throw new RuntimeException(
                    "op: division by zero in operation '" + s + "'");
         }
         push(left / right);
      }
   }

   public long tos() {
      if (stEmpty()) {
         throw new RuntimeException("tos: stack is empty");
      }
      return stack.getLast();
   }

   @Override
   public boolean equals (Object o) {
      if (!(o instanceof LongStack)) {
         return false;
      }
      LongStack other = (LongStack) o;
      return stack.equals(other.stack);
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder();

      for (Long number : stack) {
         result.append(number).append(" ");
      }

      return result.toString();
   }

   public static long interpret (String pol) {
      if (pol == null || pol.trim().isEmpty()) {
         throw new RuntimeException("Invalid expression: " + pol);
      }
      LongStack stack = new LongStack();
      String[] parts = pol.trim().split("\\s+");

      for (String part : parts) {
         if (part.equals("+") || part.equals("-") || part.equals("*") || part.equals("/")) {
            try {
               stack.op(part);
            } catch (RuntimeException e) {
               throw new RuntimeException("Invalid expression '" + pol + "': " + e.getMessage());
            }
         } else {
            try {
               stack.push(Long.parseLong(part));
            } catch (NumberFormatException e) {
               throw new RuntimeException("Invalid expression '" + pol + "': invalid symbol '" + part + "'");
            }
         }
      }
      if (stack.stEmpty() || stack.stack.size() > 1) {
         throw new RuntimeException("Invalid expression '" + pol + "'");
      }
      return stack.pop();
   }
}

//test