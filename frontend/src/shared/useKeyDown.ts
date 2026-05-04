import { useEffect } from "react";

/**
 * Custom hook to trigger an action on a specific key press
 * @param key The keyboard key to listen for (e.g., 'Escape')
 * @param action The callback function to run when the key is pressed
 */
export const useKeyDown = (key: string, action: () => void): void => {
  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === key) {
        action();
      }
    };

    window.addEventListener("keydown", onKeyDown);

    return () => {
      window.removeEventListener("keydown", onKeyDown);
    };
  }, [key, action]);
};
