export function convertEmptyStringToNull(value: unknown): unknown {
  if (typeof value === "string") {
    return value.trim() === "" ? null : value;
  }

  if (Array.isArray(value)) {
    return value.map(convertEmptyStringToNull);
  }

  if (value !== null && typeof value === "object") {
    return Object.fromEntries(
      Object.entries(value).map(([key, nestedValue]) => [
        key,
        convertEmptyStringToNull(nestedValue),
      ]),
    );
  }

  return value;
}
